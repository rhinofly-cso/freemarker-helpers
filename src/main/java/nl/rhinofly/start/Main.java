package nl.rhinofly.start;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fusesource.jansi.AnsiConsole;

/**
 * 
 * This class launches the web application in an embedded Jetty container.
 * This is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 */
public class Main {
  public static final String ANSI_CLS = "\u001b[2J";
  public static final String ANSI_HOME = "\u001b[H";
  public static final String ANSI_BOLD = "\u001b[1m";
  public static final String ANSI_AT55 = "\u001b[10;10H";
  public static final String ANSI_REVERSEON = "\u001b[7m";
  public static final String ANSI_NORMAL = "\u001b[0m";
  public static final String ANSI_WHITEONBLUE = "\u001b[37;44m";
    
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception{
    //Look for that variable and default to 8080 if it isn't there.
    String webPort = "8080";

    if(args.length > 0) {
        webPort = args[0];
    }

    String webappDirLocation = "src/main/webapp/";
    Server server = new Server(Integer.valueOf(webPort));
    WebAppContext root = new WebAppContext();

    root.setContextPath("/");
    root.setDescriptor(webappDirLocation+"/WEB-INF/web.xml");
    root.setResourceBase(webappDirLocation);

    //Parent loader priority is a class loader setting that Jetty accepts.
    //By default Jetty will behave like most web containers in that it will
    //allow your application to replace non-server libraries that are part of the
    //container. Setting parent loader priority to true changes this behavior.
    //Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
    root.setParentLoaderPriority(true);

    server.setHandler(root);

    server.start();

    AnsiConsole.systemInstall();

    AnsiConsole.out.println(ANSI_WHITEONBLUE + "Railo running on port " + webPort + ANSI_NORMAL);

    server.join();
  }
}
