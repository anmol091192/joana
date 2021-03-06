/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
package edu.kit.joana.wala.eval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class JSDGAntTask extends Task {

	private List<ResourceCollection> configs = new ArrayList<ResourceCollection>();
	private String heapSize = "128M";
	private String jar = "jSDG.jar";
	private String consoleLogDir = null;
	private int instances = 1;
	private Integer timeout = null;
	private boolean lazy = false;
	private boolean verify = false;
    private Environment env = new Environment();
    private boolean newEnvironment = false;
    private File dir = null;
//    private Redirector redirector = new Redirector(this);


	@SuppressWarnings("unchecked")
	public void execute() {
		log("jSDG Task with " + instances + " parallel instances and heap size of " + heapSize + ".");

		for (ResourceCollection rc : configs) {
			File base = new File(".");
			if (rc instanceof FileSet) {
				FileSet fs = (FileSet) rc;
				base = fs.getDir();
			}
			try {
				for (Iterator<Resource> it = rc.iterator(); it.hasNext();) {
					Resource cfg = it.next();
					String fullName;
						fullName = base.getCanonicalPath() + "/" + cfg.getName();
					runJSDG(fullName, cfg.getName());
				}
			} catch (IOException e) {
				log(e, Project.MSG_ERR);
			}

		}
	}

	private void runJSDG(String cfg, String name) throws FileNotFoundException {
		log("Creating SDG for " + name, Project.MSG_INFO);
		CommandlineJava cl = new CommandlineJava();
		cl.setMaxmemory(heapSize);
		cl.setJar(jar);
//		Path cp = cl.createClasspath(getProject());
		Argument cfgArg = cl.createArgument();
		cfgArg.setValue("-cfg");
		Argument cfgFileArg = cl.createArgument();
		cfgFileArg.setValue(cfg);

		if (timeout != null) {
			Argument to = cl.createArgument();
			to.setValue("-timeout");
			Argument toVal = cl.createArgument();
			toVal.setValue(timeout.toString());
		}

		if (lazy) {
			Argument arg = cl.createArgument();
			arg.setValue("-lazy");
		}

		if (verify) {
			Argument arg = cl.createArgument();
			arg.setValue("-verify");
		}

		executeJava(cl, name);
	}

    /**
     * Add a set of files to copy.
     * @param set a set of files to copy.
     */
    public void addFileset(FileSet set) {
        add(set);
    }

    /**
     * Add a collection of files to copy.
     * @param res a resource collection to copy.
     * @since Ant 1.7
     */
    public void add(ResourceCollection res) {
        configs.add(res);
    }

    /**
     * Set the size of the heap for the jsdg java instances. e.g. "1024M"
     * @param heapSize
     */
    public void setMemory(String heapSize) {
    	this.heapSize = heapSize;
    }

    /**
     * Sets the number of parallel running jsdg instances. (default is 1)
     * @param instances
     */
    public void setInstances(int instances) {
    	this.instances = instances;
    }

    /**
     * Sets the timeout of each jsdg instance. (default is no timeout)
     * @param timeout integer value for timeout in minutes, 0 disables timeout
     */
    public void setTimeout(int timeout) {
    	if (timeout <= 0) {
    		this.timeout = null;
    	} else {
    		this.timeout = timeout;
    	}
    }

    /**
     * Trigger lazy computation. When turned on, jsdg computed only a new SDG if
     * the output file does not exists.
     * @param lazy Lazy computation turned on when true
     */
    public void setLazy(boolean lazy) {
    	this.lazy = lazy;
    }

    /**
     * Trigger sdg verifier to check if the created sdg has syntactical flaws.
     * Set to false per default. When combined with lazy flag it can be used to simply run a
     * verification without sdg computation.
     * @param verify Verification turned on when true
     */
    public void setVerify(boolean verify) {
    	this.verify = verify;
    }

    /**
     * Add an environment variable.
     *
     * <p>Will be ignored if we are not forking a new VM.
     *
     * @param var new environment variable.
     *
     * @since Ant 1.5
     */
    public void addEnv(Environment.Variable var) {
        env.addVariable(var);
    }

    /**
     * If true, use a completely new environment.
     *
     * <p>Will be ignored if we are not forking a new VM.
     *
     * @param newenv if true, use a completely new environment.
     *
     * @since Ant 1.5
     */
    public void setNewenvironment(boolean newenv) {
        newEnvironment = newenv;
    }

    /**
     * Set the working directory of the process.
     *
     * @param d working directory.
     *
     */
    public void setDir(File d) {
        this.dir = d;
    }

    /**
     * Set the working directory of the process.
     *
     * @param d working directory.
     *
     */
    public void setJar(File d) {
        this.jar = d.getAbsolutePath();
    }

    public void setConsoleOutputDir(File d) {
    	String dir = d.getAbsolutePath();
    	if (!dir.endsWith("/")) {
    		dir += "/";
    	}
    	this.consoleLogDir = dir;
    }

    private void executeJava(CommandlineJava commandLine, String cfg) throws FileNotFoundException {
    	if (consoleLogDir != null) {
    		File logDir = new File(consoleLogDir);
    		if (!logDir.exists()) {
    			logDir.mkdirs();
    		}

    		String output = consoleLogDir + cfg + ".out";
	    	FileOutputStream stream = new FileOutputStream(output);
	    	log("Output is redirected to " + output, Project.MSG_INFO);
	    	ExecuteStreamHandler esh = new PumpStreamHandler(stream);
	    	spawn(commandLine.getCommandline(), esh, cfg);
    	} else {
	    	spawn(commandLine.getCommandline(), null, cfg);
    	}
    }

//    /**
//     * Executes the given classname with the given arguments as it
//     * were a command line application.
//     * @param command CommandlineJava.
//     */
//    private void run(CommandlineJava command, OutputStream out, String name) throws BuildException {
//        try {
//            ExecuteJava exe = new ExecuteJava();
//            exe.setJavaCommand(command.getJavaCommand());
//            exe.setClasspath(command.getClasspath());
//            exe.setSystemProperties(command.getSystemProperties());
////            exe.setPermissions(perm);
//            exe.setTimeout(timeout);
//            String cmd = "";
//            for (String str : command.getJavaCommand().getCommandline()) {
//            	cmd += str + " ";
//            }
//            log("run = " + cmd);
//            redirector.createStreams();
//            exe.execute(getProject());
//            redirector.complete();
//            if (exe.killedProcess()) {
//                throw new BuildException("Timeout: killed the sub-process");
//            }
//        } catch (IOException e) {
//            throw new BuildException(e);
//        }
//    }

    /**
     * Executes the given classname with the given arguments in a separate VM.
     * @param command String[] of command-line arguments.
     */
    private void spawn(String[] command, ExecuteStreamHandler out, String name) throws BuildException {
        Execute exe = new Execute();
        setupExecutable(exe, command);
        try {
        	if (out != null) {
        		exe.setStreamHandler(out);
        	}
            if (exe.execute() != 0) {
            	log("Error while creating SDG for " + name, Project.MSG_ERR);
            } else {
            	log("Successfully created SDG for " + name, Project.MSG_INFO);
            }
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    /**
     * Do all configuration for an executable that
     * is common across the {@link #fork(String[])} and
     * {@link #spawn(String[])} methods.
     * @param exe executable.
     * @param command command to execute.
     */
    private void setupExecutable(Execute exe, String[] command) {
        exe.setAntRun(getProject());
        setupWorkingDir(exe);
        setupEnvironment(exe);
        setupCommandLine(exe, command);
    }

    /**
     * Set up our environment variables.
     * @param exe executable.
     */
    private void setupEnvironment(Execute exe) {
        String[] environment = env.getVariables();
        if (environment != null) {
            for (int i = 0; i < environment.length; i++) {
                log("Setting environment variable: " + environment[i],
                    Project.MSG_VERBOSE);
            }
        }
        exe.setNewenvironment(newEnvironment);
        exe.setEnvironment(environment);
    }

    /**
     * Set the working dir of the new process.
     * @param exe executable.
     * @throws BuildException if the dir doesn't exist.
     */
    private void setupWorkingDir(Execute exe) {
        if (dir == null) {
            dir = getProject().getBaseDir();
        } else if (!dir.exists() || !dir.isDirectory()) {
            throw new BuildException(dir.getAbsolutePath()
                                     + " is not a valid directory",
                                     getLocation());
        }
        exe.setWorkingDirectory(dir);
    }

    /**
     * Set the command line for the exe.
     * On VMS, hands off to {@link #setupCommandLineForVMS(Execute, String[])}.
     * @param exe executable.
     * @param command command to execute.
     */
    private void setupCommandLine(Execute exe, String[] command) {
        //On VMS platform, we need to create a special java options file
        //containing the arguments and classpath for the java command.
        //The special file is supported by the "-V" switch on the VMS JVM.
        if (Os.isFamily("openvms")) {
            setupCommandLineForVMS(exe, command);
        } else {
            exe.setCommandline(command);
        }
    }

    /**
     * On VMS platform, we need to create a special java options file
     * containing the arguments and classpath for the java command.
     * The special file is supported by the "-V" switch on the VMS JVM.
     *
     * @param exe executable.
     * @param command command to execute.
     */
    private void setupCommandLineForVMS(Execute exe, String[] command) {
        ExecuteJava.setupCommandLineForVMS(exe, command);
    }
}
