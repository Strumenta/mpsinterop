package com.strumenta.mpsserver.logic;

/*Generated by MPS */

import com.strumenta.mpsserver.extensionkit.code.ExtendableServer;
import java.util.List;
import com.strumenta.mpsserver.extensionkit.code.MPSServerExtension;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import java.util.LinkedList;
import spark.Service;
import org.jetbrains.mps.openapi.module.SRepository;
import java.io.File;
import jetbrains.mps.internal.collections.runtime.IWhereFilter;
import java.util.Map;
import com.strumenta.mpsserver.extensionkit.code.Action;
import jetbrains.mps.internal.collections.runtime.MapSequence;
import java.util.HashMap;
import org.jetbrains.mps.openapi.module.SModule;
import jetbrains.mps.internal.collections.runtime.Sequence;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.EditableSModel;
import org.jetbrains.mps.openapi.model.SNodeChangeListener;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.openapi.event.SPropertyChangeEvent;
import org.jetbrains.mps.openapi.event.SReferenceChangeEvent;
import org.jetbrains.mps.openapi.event.SNodeAddEvent;
import org.jetbrains.mps.openapi.event.SNodeRemoveEvent;
import java.util.function.Consumer;
import jetbrains.mps.baseLanguage.tuples.runtime.Tuples;
import jetbrains.mps.module.ReloadableModule;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SNodeOperations;
import jetbrains.mps.baseLanguage.behavior.ClassConcept__BehaviorDescriptor;
import java.util.Objects;
import jetbrains.mps.lang.core.behavior.INamedConcept__BehaviorDescriptor;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SPointerOperations;
import jetbrains.mps.smodel.SNodePointer;
import java.lang.reflect.Constructor;
import jetbrains.mps.baseLanguage.tuples.runtime.MultiTuple;
import spark.Route;
import spark.Request;
import spark.Response;
import spark.Filter;
import spark.ExceptionHandler;
import jetbrains.mps.internal.collections.runtime.ISelector;
import org.jetbrains.mps.openapi.language.SConcept;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;

public class Server implements ExtendableServer {

  private List<MPSServerExtension> registeredExtensions = ListSequence.fromList(new LinkedList<MPSServerExtension>());
  private Service sparkService;
  public static int NOT_FOUND = 404;

  public void registerExtension(MPSServerExtension extension) {
    ListSequence.fromList(registeredExtensions).addElement(extension);
  }

  public void activateAndRegisterExtension(final MPSServerExtension extension) {
    extension.activate(this);
    this.registerExtension(extension);
  }

  public List<MPSServerExtension> getRegisteredExtensions() {
    return registeredExtensions;
  }

  public int getPort() {
    return serverConfiguration.getPort();
  }

  public SRepository getRepo() {
    return this.serverConfiguration.getRepo();
  }

  public static class ServerConfiguration {
    private int port = DEFAULT_PORT;
    private File gitRoot = null;
    private SRepository repo = null;
    private boolean autosave = true;
    private String gitUsername = null;
    private String gitPassword = null;
    private List<String> extensionPaths = ListSequence.fromList(new LinkedList<String>());

    public ServerConfiguration(SRepository repo) {
      this.repo = repo;
    }

    public SRepository getRepo() {
      return repo;
    }

    public ServerConfiguration port(int port) {
      this.port = port;
      return this;
    }
    public int getPort() {
      return this.port;
    }

    public ServerConfiguration gitRoot(File gitRoot) {
      this.gitRoot = gitRoot;
      return this;
    }

    public ServerConfiguration gitCredentials(String gitUsername, String gitPassword) {
      this.gitUsername = gitUsername;
      this.gitPassword = gitPassword;
      return this;
    }

    public ServerConfiguration autosave(boolean autosave) {
      this.autosave = autosave;
      return this;
    }

    public void print() {
      System.out.println("Server configuration");
      System.out.println("  Git root        : " + gitRoot);
      System.out.println("  Port            : " + port);
      System.out.println("  Autosave        : " + autosave);
      System.out.println("  Extension paths : " + extensionPaths);
    }

    public void addExtensionPath(String path) {
      ListSequence.fromList(this.extensionPaths).addElement(path);
    }

    public boolean isRelevantExtensionModulePath(final String moduleName) {
      return ListSequence.fromList(extensionPaths).any(new IWhereFilter<String>() {
        public boolean accept(String ep) {
          return moduleName.startsWith(ep);
        }
      });
    }
  }

  public static class IssueSettingPortException extends RuntimeException {
    public IssueSettingPortException(Throwable cause) {
      super("An issue prevented from setting the desired port", cause);
    }
  }

  private DataExposer data;
  private ServerController controller;
  private final ServerConfiguration serverConfiguration;
  private IntentionsIntegrationServerModule intentionsModule;

  private static Server INSTANCE = null;
  public static int DEFAULT_PORT = 2904;

  private Map<String, Map<String, Action>> conceptSpecificActions = MapSequence.fromMap(new HashMap<String, Map<String, Action>>());

  @Override
  public void registerConceptSpecificAction(String conceptName, String actionName, Action action) {
    if (!(MapSequence.fromMap(conceptSpecificActions).containsKey(conceptName))) {
      MapSequence.fromMap(conceptSpecificActions).put(conceptName, MapSequence.fromMap(new HashMap<String, Action>()));
      MapSequence.fromMap(MapSequence.fromMap(conceptSpecificActions).get(conceptName)).put(actionName, action);
    }
  }

  public Server(final ServerConfiguration serverConfiguration) {
    this.serverConfiguration = serverConfiguration;
    this.data = new DataExposer(serverConfiguration.repo);
    this.controller = new ServerController(serverConfiguration.repo, this.data);
    if (serverConfiguration.autosave) {
      setupAutoSave();
    }
    intentionsModule = new IntentionsIntegrationServerModule(serverConfiguration.repo);
  }

  private void setupAutoSave() {
    serverConfiguration.repo.getModelAccess().runReadAction(new Runnable() {
      public void run() {
        for (SModule module : Sequence.fromIterable(serverConfiguration.repo.getModules())) {
          for (final SModel model : Sequence.fromIterable(module.getModels())) {
            if (model instanceof EditableSModel) {
              model.addChangeListener(new SNodeChangeListener() {
                private void save() {
                  SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                      serverConfiguration.repo.getModelAccess().runWriteAction(new Runnable() {
                        public void run() {
                          ((EditableSModel) model).save();
                        }
                      });
                    }
                  });
                }

                @Override
                public void propertyChanged(@NotNull SPropertyChangeEvent p0) {
                  save();
                }

                @Override
                public void referenceChanged(@NotNull SReferenceChangeEvent p0) {
                  save();
                }

                @Override
                public void nodeAdded(@NotNull SNodeAddEvent p0) {
                  save();
                }

                @Override
                public void nodeRemoved(@NotNull SNodeRemoveEvent p0) {
                  save();
                }
              });
            }
          }
        }
      }
    });
  }

  public static void destroy() {
    if (INSTANCE != null) {
      INSTANCE.kill();
      INSTANCE = null;
    }
  }

  public static class OperationResult {
    public boolean success;
    public String message;
    public Object value;

    public static OperationResult success() {
      return success(null);
    }

    public static OperationResult success(Object value) {
      OperationResult instance = new OperationResult();
      instance.message = "ok";
      instance.success = true;
      instance.value = value;
      return instance;
    }


    public static OperationResult failure(String message) {
      OperationResult instance = new OperationResult();
      instance.message = message;
      instance.success = false;
      instance.value = null;
      return instance;
    }

    public static OperationResult failure(Throwable throwable) {
      String message = throwable.getMessage();
      if (message == null || message.isBlank()) {
        message = throwable.getClass().getCanonicalName();
      }
      return failure(message);
    }
  }

  public static Server launch(final ServerConfiguration serverConfiguration) {
    return launch(serverConfiguration, new Consumer<Tuples._2<Server, Runnable>>() {
      @Override
      public void accept(Tuples._2<Server, Runnable> t) {
        t._1().run();
      }
    });
  }

  private void loadExtensions(final SRepository repo, final Runnable followUp, final ServerConfiguration serverConfiguration) {
    this.log("Waiting to start loading extensions");
    repo.getModelAccess().runReadAction(new Runnable() {
      public void run() {
        Server.this.log("Start of loading extensions");
        for (SModule module : Sequence.fromIterable(repo.getModules())) {
          if (serverConfiguration.isRelevantExtensionModulePath(module.getModuleName()) && module instanceof ReloadableModule) {
            Server.this.log("  extension loading, module " + module.getModuleName());
            ReloadableModule reloadableModule = ((ReloadableModule) module);
            for (SModel model : Sequence.fromIterable(module.getModels())) {
              for (SNode rootNode : Sequence.fromIterable(model.getRootNodes())) {
                if (rootNode.isInstanceOfConcept(CONCEPTS.ClassConcept$IY)) {
                  try {
                    SNode rootClass = SNodeOperations.cast(rootNode, CONCEPTS.ClassConcept$IY);
                    if (ListSequence.fromList(ClassConcept__BehaviorDescriptor.getAllSuperClassifiers_id4fAeKISQjDi.invoke(rootClass)).any(new IWhereFilter<SNode>() {
                      public boolean accept(SNode it) {
                        return Objects.equals(INamedConcept__BehaviorDescriptor.getFqName_idhEwIO9y.invoke(it), INamedConcept__BehaviorDescriptor.getFqName_idhEwIO9y.invoke(SPointerOperations.resolveNode(new SNodePointer("r:0b41bd2a-d72e-4e3a-8bde-c68965ce30dd(com.strumenta.mpsserver.extensionkit.code)", "3248405667824468818"), repo)));
                      }
                    })) {
                      Server.this.log("-> Extension class found " + INamedConcept__BehaviorDescriptor.getFqName_idhEwIO9y.invoke(rootClass));
                      String qname = INamedConcept__BehaviorDescriptor.getFqName_idhEwIO9y.invoke(rootClass);
                      Class clazz = reloadableModule.getClassLoader0().loadClass(qname);
                      for (Constructor<?> constructor : clazz.getConstructors()) {
                        if (constructor.getParameterCount() == 0) {
                          Object instance = constructor.newInstance();
                          if (instance instanceof MPSServerExtension) {
                            MPSServerExtension mpsServerExtension = ((MPSServerExtension) instance);
                            try {
                              Server.this.activateAndRegisterExtension(mpsServerExtension);
                            } catch (Throwable t) {
                              t.printStackTrace();
                              Server.this.log("Error while activating extension " + INamedConcept__BehaviorDescriptor.getFqName_idhEwIO9y.invoke(rootClass));
                            }
                          }
                        }
                      }
                    }
                  } catch (Throwable t) {
                    t.printStackTrace();
                    Server.this.log("Error while searching for extensions");
                  }
                }
              }
            }
          }
        }
        Server.this.log("End of loading extensions");
        followUp.run();
      }
    });
  }


  public static Server launchWithExtensionsLoading(final ServerConfiguration serverConfiguration) {
    return Server.launch(serverConfiguration, new Consumer<Tuples._2<Server, Runnable>>() {
      @Override
      public void accept(Tuples._2<Server, Runnable> t) {
        t._0().loadExtensions(serverConfiguration.getRepo(), t._1(), serverConfiguration);
      }
    });
  }


  public static Server launch(final ServerConfiguration serverConfiguration, Consumer<Tuples._2<Server, Runnable>> prelaunch) {
    destroy();
    INSTANCE = new Server(serverConfiguration);
    Runnable r = new Runnable() {
      @Override
      public void run() {
        new Thread(new Runnable() {
          @Override
          public void run() {
            INSTANCE.log("ready to start");
            int nRetries = 5;
            boolean success = false;
            while (!(success)) {
              try {
                INSTANCE.start();
                success = true;
              } catch (IssueSettingPortException e) {
                if (nRetries > 0) {
                  System.out.println("Issue setting port, retry... nRetries=" + nRetries);
                  nRetries--;
                } else {
                  throw new RuntimeException(e);
                }
              }
            }
          }
        }).start();
      }
    };
    INSTANCE.log("initiating prelaunch");
    prelaunch.accept(MultiTuple.<Server,Runnable>from(INSTANCE, r));
    return INSTANCE;
  }

  public void waitForStop() {
    try {
      Thread.sleep(Long.MAX_VALUE);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private void routesForModels() {
    new ModelRoutes(serverConfiguration.repo, data, conceptSpecificActions).addRoutes(sparkService);
  }

  private void routesForModules() {
    new ModuleRoutes(serverConfiguration.repo, data, conceptSpecificActions).addRoutes(sparkService);
  }

  private boolean hasGit() {
    return serverConfiguration.gitRoot != null;
  }

  private GitFacade gitFacade() {
    try {
      if (serverConfiguration.gitRoot == null) {
        throw new IllegalStateException("No git enabled");
      }
      GitFacade gitFacade = new GitFacade(serverConfiguration.gitRoot);
      if (serverConfiguration.gitUsername != null) {
        gitFacade.setCredentials(serverConfiguration.gitUsername, serverConfiguration.gitPassword);
      }
      return gitFacade;
    } catch (RuntimeException e) {
      e.printStackTrace();
      System.out.println("GIT FACADE DISABLED");
      return null;
    }
  }

  private void routesForGlobalLevel() {
    new GlobalLevelRoutes(serverConfiguration.repo, data, conceptSpecificActions, (hasGit() ? gitFacade() : null)).addRoutes(sparkService);
  }

  private void start() {
    sparkService = Service.ignite();
    WebSocketHandler.serverController = this.controller;
    try {
      sparkService.webSocket("/socket", new WebSocketHandler(intentionsModule));
      sparkService.port(serverConfiguration.port);
      sparkService.init();
    } catch (Exception e) {
      //  TODO, retry 
      System.out.println("Issue setting port");
      try {
        Thread.sleep(250);
      } catch (InterruptedException e2) {
      }
      throw new IssueSettingPortException(e);
    }

    sparkService.options("/*", new Route() {
      @Override
      public Object handle(Request request, Response response) throws Exception {
        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
        if (accessControlRequestHeaders != null) {
          response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
        }
        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
        if (accessControlRequestMethod != null) {
          response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
        }
        return "OK";
      }
    });
    sparkService.before(new Filter() {
      @Override
      public void handle(Request request, Response response) throws Exception {
        response.header("Access-Control-Allow-Origin", "*");
      }
    });
    sparkService.exception(NotFoundException.class, new ExceptionHandler<Exception>() {
      @Override
      public void handle(Exception e, Request p1, Response p2) {
        p2.status(NOT_FOUND);
      }
    });
    sparkService.exception(Exception.class, new ExceptionHandler<Exception>() {
      @Override
      public void handle(Exception e, Request p1, Response p2) {
        System.out.println("EXCEPTION");
        e.printStackTrace();
      }
    });

    sparkService.get("/", new Route() {
      @Override
      public Object handle(Request p0, Response res) throws Exception {
        return "MPS Server up and running.";
      }
    });
    sparkService.get("/server/extensions", new Route() {
      @Override
      public Object handle(Request p0, Response res) throws Exception {
        return OperationResult.success(ListSequence.fromList(registeredExtensions).select(new ISelector<MPSServerExtension, String>() {
          public String select(MPSServerExtension it) {
            return it.name();
          }
        }).toListSequence());
      }
    }, new JsonTransformer());
    routesForGlobalLevel();
    routesForModules();
    routesForModels();
    new NodesRoutes(serverConfiguration.repo, data).addRoutes(sparkService);
    new ConceptRoutes(serverConfiguration.repo, data).addRoutes(sparkService);
    new IntentionRoutes(serverConfiguration.repo, data, intentionsModule).addRoutes(sparkService);
  }

  private void kill() {
    sparkService.stop();
    sparkService = null;
  }

  public static boolean isRunning() {
    return INSTANCE != null;
  }

  public void log(String msg) {
    System.out.println("MPSServer: " + msg);
  }

  private static final class CONCEPTS {
    /*package*/ static final SConcept ClassConcept$IY = MetaAdapterFactory.getConcept(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0xf8c108ca66L, "jetbrains.mps.baseLanguage.structure.ClassConcept");
  }
}