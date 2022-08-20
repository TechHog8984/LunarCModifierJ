import java.io.File;
import java.io.FileNotFoundException;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Main {
    private static String AppdataPath = System.getenv("APPDATA");
    private static String ProgramsPath = AppdataPath + "\\..\\Local\\Programs";
    private static String ResourcesPath = ProgramsPath + "\\lunarclient\\resources";
    private static String AppPath = ResourcesPath + "\\app.asar";
    private static String BackupPath = ResourcesPath + "\\app.asar.LCMJ_backup";
    private static String DecompiledPath = ResourcesPath + "\\decompiled";

    private static boolean fileExists(String Path) {
        boolean exists = false;
        try {
            File file = new File(Path);
            Scanner scanner = new Scanner(file);

            scanner.close();
            if (scanner != null) {
                exists = true;
            };
        } catch (FileNotFoundException e) {
            exists = false;
        };

        return exists;
    }

    private static void openDirectory() throws IOException,InterruptedException {
        // Runtime.getRuntime().exec("explorer " + ResourcesPath); // apparently deprecated

        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", "explorer \"" + ResourcesPath + '"');

        try {
            Process proc = builder.start();

            proc.waitFor();
            proc.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        };
    }
    private static void backupAsar() throws IOException {
        if (fileExists(BackupPath)) {
            System.out.println("Backup already exists!");
            // return false;
            return;
        };
        
        if (!fileExists(AppPath)) {
            System.out.println("No app.asar, try re-installing the Lunar Client launcher!");
            // return false;
            return;
        };


        FileReader appReader = null;
        FileWriter backupWriter = null;
        try {

            File app = new File(AppPath);
            if (!app.exists() || !app.canRead()) {
                System.out.println("app.asar does not exist or cannot be read from!");
                // return false;
                return;
            };

            appReader = new FileReader(AppPath, Charset.forName("ISO-8859-1")); //there is no actual problem here, probably something with the java extension.
            backupWriter = new FileWriter(BackupPath, Charset.forName("ISO-8859-1")); //there is no actual problem here, probably something with the java extension.

            int character;
            while ((character = appReader.read()) != -1) {
                backupWriter.write(character);
            };

            System.out.println("Successfully wrote to backup.asar.");

        } catch(Exception e) {
            System.out.println("An error occured: " + e);
            // return false;
            return;
        } finally {
            if (appReader != null) {
                appReader.close();
            }
            if (backupWriter != null) {
                backupWriter.close();
            }
        };

        // return false;
    }
    private static void restoreBackup() throws IOException {
        if (!fileExists(BackupPath)) {
            System.out.println("Backup does not exist!");
            return;
        };

        FileReader backupReader = null;
        FileWriter appWriter = null;
        
        try {
            File backup = new File(BackupPath);
            if (!backup.exists() || !backup.canRead()) {
                System.out.println("app.asar.LCMJ_backup does not exist or cannot be read from!");
                return;
            };

            backupReader = new FileReader(BackupPath, Charset.forName("ISO-8859-1")); //there is no actual problem here, probably something with the java extension.
            appWriter = new FileWriter(AppPath, Charset.forName("ISO-8859-1")); //there is no actual problem here, probably something with the java extension.

            int character;
            while ((character = backupReader.read()) != -1) {
                appWriter.write(character);
            };

            System.out.println("Successfully wrote to app.asar.");
        } catch(Exception e) {
            System.out.println("An error occured: " + e);
            return;
        } finally {
            if (backupReader != null) {
                backupReader.close();
            }
            if (appWriter != null) {
                appWriter.close();
            }
        };
    }
    private static void decompile() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", "npx asar pack " + AppPath + " " + DecompiledPath);

        File app = new File(AppPath);
        if (!app.exists() || !app.canRead()) {
            System.out.println("app.asar does not exist or cannot be read from!");
            return;
        };

        try {
            Process proc = builder.start();

            proc.waitFor();
            proc.destroy();

            System.out.println("Successfully decompiled app.asar!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        };
    }
    private static void compile() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", "npx asar extract " + AppPath + " " + DecompiledPath);

        try {
            Process proc = builder.start();

            proc.waitFor();
            proc.destroy();

            System.out.println("Successfully compiled!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        };
    }

    private static void printOptions() {
        System.out.println("LunarCModifier [Java port] by TechHog#8984\nCredits to the creator of https://github.com/SK3-4121/SpotiLight");
        System.out.println("\n[0] Exit: Closes the program.");
        System.out.println("[1] Help: lists the valid options.");
        System.out.println("[2] Open directory: opens the directory where everything is stored.");
        System.out.println("[3] BackupAsar: Backs up app.asar so you can revert any changes made.");
        System.out.println("[4] RestoreBackup: Restores your backup of app.asar, restoring the app to its backed-up state.");
        System.out.println("[5] Decompile: Decompiles the Lunar Client launcher, and outputs it into a folder called 'decompiled'.");
        System.out.println("[6] Compile: Compiles the decompiled code, which you are expected to modify.");
    }
    
    private static boolean prompt(Scanner scanner) throws IOException,InterruptedException {
        System.out.print("\nEnter your choice: ");
        String text = scanner.nextLine();

        boolean ValidOption = true;
        int NumberChoice = -1;

        try {
            NumberChoice = Integer.parseInt(text);
            System.out.println(NumberChoice);
        } catch(Exception e) {
            ValidOption = false;
        }

        if (!ValidOption || NumberChoice == -1) {
            System.out.println("Invalid option");
            return true;
        };
        
        switch(NumberChoice){
            case 0:
                return false;
            
            case 1:
                printOptions();
                break;

            case 2:
                openDirectory();
                break;

            case 3:
                backupAsar();
                break;

            case 4:
                restoreBackup();
                break;

            case 5:
                decompile();
                break;

            case 6:
                compile();
                break;
        };

        return true;
    }

    public static void main(String[] args) throws IOException,InterruptedException {
        printOptions();
        Scanner scanner = new Scanner(System.in);
        boolean active = true;
        while (active) {
            active = prompt(scanner);
        };

        scanner.close();
    }

}