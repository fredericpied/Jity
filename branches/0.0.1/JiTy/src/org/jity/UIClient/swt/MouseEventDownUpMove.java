package org.jity.UIClient.swt;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MouseEventDownUpMove {
  public static void main(String[] args) {
    Display display = new Display();
    final Shell shell = new Shell(display);

    Listener listener = new Listener() {
      public void handleEvent(Event event) {
        switch (event.type) {
        case SWT.MouseDown:
          System.out.println("down:" + event);
          break;
        case SWT.MouseMove:
          System.out.println("move:"+event);
          break;
        case SWT.MouseUp:
          System.out.println("Up:"+event);
          break;
        }
      }
    };
    shell.addListener(SWT.MouseDown, listener);
    shell.addListener(SWT.MouseUp, listener);
    shell.addListener(SWT.MouseMove, listener);
    shell.setSize(300, 300);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}