package com.jcaa.usersmanagement;

import com.jcaa.usersmanagement.infrastructure.config.DependencyContainer;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.UserManagementCli;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import java.util.Scanner;
import java.util.logging.Logger;

public final class Main {

  private static final Logger log = Logger.getLogger(Main.class.getName());

  public static void main(final String[] args) {
    log.info("Starting Users Management System...");
    runApplication();
  }

  private static void runApplication() {
    try (Scanner scanner = new Scanner(System.in)) {
      final DependencyContainer container = buildContainer();
      final ConsoleIO consoleIO = buildConsole(scanner);
      final UserManagementCli cli = buildCli(container, consoleIO);

      cli.start();
    } catch (Exception e) {
      log.severe("Critical error starting application: " + e.getMessage());
    }
  }

  private static DependencyContainer buildContainer() {
    return new DependencyContainer();
  }

  private static ConsoleIO buildConsole(Scanner scanner) {
    return new ConsoleIO(scanner, System.out);
  }

  private static UserManagementCli buildCli(DependencyContainer container, ConsoleIO consoleIO) {
    return new UserManagementCli(container.userController(), consoleIO);
  }
}