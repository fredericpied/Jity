package org.jity.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import org.jity.common.commandLine.BadArgCLException;
import org.jity.common.commandLine.CommandLine;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.util.TimeUtil;

public class Main extends CommandLine {
	private static final Logger logger = Logger.getLogger(Main.class);
	
	private Main() {
		this.setHelpMessage("JiTy Server command line options:" +
				"\n-start\t\tStart the JiTy Server" +
				"\n-stop\t\tStop the JiTy Server");
		this.setUsage("jityServer [-stop|-start]");
		this.setMinArgsNumber(1);
		this.setMaxArgsNumber(1);
	}

	private static int start() {
		Server.getInstance().startServer();
		return 0;
	}
	
	private static int stop() throws InterruptedException, UnknownHostException, IOException {
		logger.info("Reading configuration file");
		ServerConfig.getInstance().initialize();
					
		JityRequest request = new JityRequest();
		request.setInstructionName("SHUTDOWNSERVER");
		
		try {
			RequestSender requestLauncher = new RequestSender();
			requestLauncher.openConnection("localhost", ServerConfig.getInstance().getSERVER_UI_INPUT_PORT());
			JityResponse response = requestLauncher.sendRequest(request);
			requestLauncher.closeConnection();
			
			if (! response.isInstructionResultOK()) {
				if (!response.getExceptionName().equals("java.net.SocketException")) {
					logger.error(response.getExceptionName()+": "+response.getExceptionMessage());
					logger.fatal("Failed to stop JiTy Server");
					return 1;
				}
			}

			logger.info("JiTy Server stopped");
			return 0;
		
		} catch (ConnectException e) {
			logger.error("JiTy Server not running on localhost");
			return 0;
		}
		
	}
	
	private static int launch(String[] args) throws BadArgCLException, InterruptedException, UnknownHostException, IOException {

		//this.loadArguments(args);

		if (args.length != 1) {
			throw new BadArgCLException("Incorrect arguments number (require 1 argument)");
		}
		
		if (args[0].equals("-stop")) {

			return stop();
			
		} else if (args[0].equals("-start")) {
			
			return start();
			
		} else if (args[0].equals("-restart")) {
		
			int status = stop();
			
			if (status != 0) return status;
			
			TimeUtil.waiting(2);
			
			return start();
			
		} else {
			throw new BadArgCLException(args[0]+" is not a valid parameter");
		}
		
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		
		try {

			int returnCode = main.launch(args);
			System.exit(returnCode);
			
		} catch (BadArgCLException e){
			main.showHelpMessage(e.getClass().getSimpleName()+": "+e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
