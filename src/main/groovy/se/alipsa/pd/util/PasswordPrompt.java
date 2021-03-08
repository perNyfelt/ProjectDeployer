package se.alipsa.pd.util;

import java.io.*;

public class PasswordPrompt {

  public static void main(String[] args) throws IOException {
    String pwd = readPassword("Enter password: ");
    System.out.println("The password entered was " + pwd);
  }


  public static String readPassword(String prompt) {
    java.io.Console console = System.console();
    if (console == null) {
      System.err.println("Failed to retrieve console, trying System.in");
      return getPassword(prompt);
    }
    return String.valueOf(console.readPassword(prompt));
  }

  private static String getPassword(String prompt) {

    String password = "";
    ConsoleEraser consoleEraser = new ConsoleEraser();
    System.out.print(prompt);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    consoleEraser.start();
    try {
      password = in.readLine();
    }
    catch (IOException e){
      System.out.println("Error trying to read your password!");
      System.exit(1);
    }

    consoleEraser.halt();
    System.out.print("\b");

    return password;
  }


  private static class ConsoleEraser extends Thread {
    private boolean running = true;
    public void run() {
      while (running) {
        System.out.print("\b ");
        try {
          Thread.currentThread().sleep(1);
        }
        catch(InterruptedException e) {
          break;
        }
      }
    }
    public synchronized void halt() {
      running = false;
    }
  }
}
