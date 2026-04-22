package com.jcaa.usersmanagement;

import com.jcaa.usersmanagement.infrastructure.config.DependencyContainer;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.UserManagementCli;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(final String[] args) {
    log.info("Starting Users Management System...");
    runApplication();
  }

  private static void runApplication() {
    DependencyContainer container = buildContainer();
    startCli(container);
  }

  private static DependencyContainer buildContainer() {
    return new DependencyContainer();
  }

  private static void startCli(DependencyContainer container) {
    try (Scanner scanner = new Scanner(System.in)) {
      ConsoleIO consoleIO = buildConsole(scanner);
      UserManagementCli cli = buildCli(container, consoleIO);
      cli.start();
    }
  }

  private static ConsoleIO buildConsole(Scanner scanner) {
    return new ConsoleIO(scanner, System.out);
  }

  private static UserManagementCli buildCli(DependencyContainer container, ConsoleIO consoleIO) {
    return new UserManagementCli(container.userController(), consoleIO);
  }
}