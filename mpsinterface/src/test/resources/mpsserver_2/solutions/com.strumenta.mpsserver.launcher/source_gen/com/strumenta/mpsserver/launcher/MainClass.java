package com.strumenta.mpsserver.launcher;

/*Generated by MPS */

import java.util.Objects;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Priority;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import java.io.File;
import com.strumenta.mpsserver.logic.Server;
import jetbrains.mps.tool.environment.Environment;
import jetbrains.mps.project.Project;

public class MainClass {

  private static void considerExtraLogging() {
    String enableExtraLogging = System.getenv("MPSSERVER_EXTRALOGGING");
    if (Objects.equals(enableExtraLogging, "enabled") || Objects.equals(enableExtraLogging, "true")) {
      ConsoleAppender consoleAppender = new ConsoleAppender();
      consoleAppender.setThreshold(Priority.DEBUG);
      LogManager.getRootLogger().setLevel(Level.DEBUG);
      LogManager.getRootLogger().addAppender(consoleAppender);
    }
  }

  private static int getPort() {
    int port = 2904;
    String mpsServerPortStr = System.getenv("MPSSERVER_PORT");
    if (mpsServerPortStr != null && !(mpsServerPortStr.isBlank())) {
      port = Integer.parseInt(mpsServerPortStr);
      System.out.println("USING MPS SERVER PORT: " + port);
    } else {
      System.out.println("USING DEFAULT MPS SERVER PORT: " + port);
    }
    return port;
  }

  private static File getProjectFile() {
    considerExtraLogging();
    String projectFilePath = System.getenv("MPSSERVER_PROJECT_FILE_PATH");
    if (projectFilePath == null) {
      System.out.println("NO PROJECT SPECIFIED, DEFAULTING TO CURRENT DIRECTORY");
      projectFilePath = "";
    } else {
      System.out.println("PROJECT SPECIFIED: " + projectFilePath);
    }

    File projectFile = new File(projectFilePath).getAbsoluteFile();
    System.out.println("Project file " + projectFile);

    return projectFile;
  }

  private static File getGitRoot(File projectFile) {
    String gitRootPath = System.getenv("MPSSERVER_GIT_ROOT");
    if (gitRootPath == null) {
      gitRootPath = projectFile.getAbsolutePath();
    }

    File dotGitDir = new File(gitRootPath + File.separator + ".git");
    if (dotGitDir.exists()) {
      System.out.println("Git root path " + gitRootPath);
      return new File(gitRootPath);
    }
    System.out.println("No Git root");
    return null;
  }
  private static boolean getAutoSave() {
    String autosave = System.getenv("MPSSERVER_AUTOSAVE");
    if (autosave == null) {
      autosave = "true";
    }

    System.out.println("Autosave " + autosave);
    return Boolean.parseBoolean(autosave);
  }

  private static void considerGitCredentials(Server.ServerConfiguration serverConfiguration) {
    String gitCredentials = System.getenv("MPSSERVER_GIT_CREDENTIALS");
    if (gitCredentials != null && (gitCredentials != null && gitCredentials.length() > 0)) {
      System.out.println("Git credentials found");
      String[] parts = gitCredentials.split(":");
      System.out.println("  git username: " + parts[0]);
      System.out.println("  git password: " + parts[1]);
      serverConfiguration.gitCredentials(parts[0], parts[1]);
    } else {
      System.out.println("No Git credentials");
    }
  }


  private static void considerExtensionPath(Server.ServerConfiguration serverConfiguration) {
    String ep = System.getenv("MPSSERVER_EXTENSION_PATH");
    if (ep == null) {
      System.out.println("NO EXTENSION PATH SPECIFIED, NO EXTENSIONS WILL BE LOADED");
    } else {
      for (String entry : ep.split(",")) {
        System.out.println("EXTENSION PATH CONSIDERED: '" + entry + "'");
        serverConfiguration.addExtensionPath(entry);
      }
    }
  }

  public static void mpsMain(Environment ideaEnvironment) {
    considerExtraLogging();
    File projectFile = getProjectFile();
    Project project = ideaEnvironment.openProject(projectFile);
    System.out.println("Project " + project);
    final Server.ServerConfiguration serverConfiguration = new Server.ServerConfiguration(project.getRepository()).port(getPort()).gitRoot(getGitRoot(projectFile)).autosave(getAutoSave());
    considerGitCredentials(serverConfiguration);
    considerExtensionPath(serverConfiguration);
    serverConfiguration.print();

    System.out.println("[MPS Server - Start]");
    try {
      Server sc = Server.launchWithExtensionsLoading(serverConfiguration);
      System.out.println("WAIT FOR STOP");
      sc.waitForStop();
      System.out.println("WAITED FOR STOP, RETURNED");
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.out.println("[MPS Server - End]");
  }
}
