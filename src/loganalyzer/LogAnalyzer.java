package loganalyzer;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import loganalyzer.filter.exceptions.InterpretException;


/**
 *
 * @author cz2b10w5
 */
public class LogAnalyzer {
    
    private static Map<String, IModule> modules = new HashMap<>();

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, InterpretException {
        // TEST - REMOVE
        IModule mod = new OpenStageModule();
        modules.put(mod.getModuleName(), mod);

        // TEST - REMOVE
        if (args.length < 2) {
            usage();
            System.exit(0);
        } else {
            if (args[0].compareTo("-m") == 0) {
                if (args[1].compareTo("list") == 0) {
                    for(IModule module : modules.values()) {
                        System.out.println('\t' + module.getModuleName());
                    }
                } else {
                    IModule module = modules.get(args[1]);
                    if (module != null) {
                        module.perform(Arrays.copyOfRange(args, 2, args.length));
                    }
                }
            }           
        } 
        

    }

    private static void usage() {
        System.out.println(System.getProperty("sun.java.command") + " inputFile");
        System.out.println("-m modul_name - user specific parser. If you call -m list it will show all modules");
    }
}
