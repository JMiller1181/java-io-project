import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        while (run) {
            System.out.println("""
                    Hello, welcome to file manager.
                    What would you like to do?
                    1) SEARCH - searches for a file
                    2) DISPLAY - displays directory/file data
                    3) FILES - create, delete, copy, or move files
                    4) DIRECTORIES - create or delete directories
                    All other options exit file manager.""");
            int mainMenuOption = scanner.nextInt();
            scanner.nextLine();
            switch (mainMenuOption) {
                case 1:
                    System.out.println("What is the file path of the" +
                            " directory you would like to search?\n" +
                            "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                    String searchPathString = scanner.nextLine();
                    Path searchPath = Paths.get(searchPathString);
                    System.out.println("What would you like to search for?");
                    String searchQuery = scanner.nextLine();
                    Main.searchFiles(searchPath, searchQuery);
                    break;
                case 2:
                    System.out.println("What directory or file do you want to display?\n" +
                            "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                    String displayPathString = scanner.nextLine();
                    Path displayPath = Paths.get(displayPathString);
                    System.out.println(Main.getPathInfo(displayPath));
                    break;
                case 3:
                    System.out.println("""
                            What would you like to do?
                            1) CREATE
                            2) DELETE
                            3) COPY
                            4) MOVE
                            All other options exit file manager.""");
                    int fileMenuOption = scanner.nextInt();
                    scanner.nextLine();
                    switch (fileMenuOption) {
                        case 1:
                            Main.createFile();
                            break;
                        case 2:
                            System.out.println("What file do you want to delete?\n" +
                                    "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                            String deletePathString = scanner.nextLine();
                            Path deletePath = Paths.get(deletePathString);
                            Main.deleteAFile(deletePath);
                            break;
                        case 3:
                            System.out.println("Which file would you like to copy?\n" +
                                    "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                            String copyFileSourceString = scanner.nextLine();
                            System.out.println("Where would you like to copy to?\n" +
                                    "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                            String copyDestinationFileString = scanner.nextLine();
                            Path copyFileSourcePath = Paths.get(copyFileSourceString);
                            Path copyFileDestinationPath = Paths.get(copyDestinationFileString);
                            Main.copyFile(copyFileSourcePath, copyFileDestinationPath);
                            break;
                        case 4:
                            System.out.println("Which file would you like to move?\n" +
                                    "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                            String moveFileSourceString = scanner.nextLine();
                            System.out.println("Where would you like to move the file to?\n" +
                                    "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                            String moveFileDestinationString = scanner.nextLine();
                            Path moveFileSourcePath = Paths.get(moveFileSourceString);
                            Path moveFileDesinationPath = Paths.get(moveFileDestinationString);
                            Main.moveFile(moveFileSourcePath, moveFileDesinationPath);
                            break;
                        default: run = false;
                            break;
                    }
                    break;
                case 4:
                    System.out.println("""
                            What would you like to do?
                            1) CREATE
                            2) DELETE
                            All other options exit file manager.""");
                    int directoryMenuOption = scanner.nextInt();
                    scanner.nextLine();
                    switch (directoryMenuOption) {
                        case 1:
                            System.out.println("Where would you like to create your directory?\n" +
                                    "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                            String createBaseDir = scanner.nextLine();
                            System.out.println("What would you like to name your directory?");
                            String createDirName = scanner.nextLine();
                            Path createBaseDirPath = Paths.get(createBaseDir);
                            Main.createDirectory(createBaseDirPath, createDirName);
                            break;
                        case 2:
                            System.out.println("What directory do you want to delete?\n" +
                                    "Please type out entire file path. Example: src/mainDirectory/dir/numFile.txt");
                            String deleteDirPathString = scanner.nextLine();
                            Path deleteDirPath = Paths.get(deleteDirPathString);
                            Main.deleteAFile(deleteDirPath);
                            break;
                        default: run = false;
                            break;
                    }
                    break;
                default: run = false;
                    break;
            }
        }
    }


    /**
     * @param path the specified directory or file
     * @return Information on the directory and all files in it
     */
    public static String getPathInfo(Path path) {
        String pathData = "Target file name: " + path.getFileName() + "\n";
        try {
            pathData += "Files in path:\n";

            for (int i = 0; i < path.getNameCount(); i++) {
                pathData += path.getName(i) + "/";
            }
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            File targetFile = new File(path.toUri());
            if (!attributes.isDirectory()) {
                pathData += "\nFile size: " + attributes.size() + " bytes\n";
                pathData += "File creation time: " + attributes.creationTime() + "\n";
                pathData += "Last modified: " + attributes.lastModifiedTime() + "\n";
            } else {
                pathData += "\nContents of directory:\n";
                File[] fileContents = targetFile.listFiles();
                try {
                    for (File file : fileContents) {
                        if (file.isDirectory()) {
                            pathData += "\nDirectory: " + file.getName() + "\n";
                        } else {
                            pathData += "\nFile: " + file.getName() + "\n";
                            pathData += getPathInfo(file.toPath());
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Error getting file information.");
            System.out.println(e);
        }
        return pathData;
    }

    /**
     * Creates a directory in the designated directory
     *
     * @param baseDir the designated directory
     * @param dirName the name of the directory to be created
     */
    public static void createDirectory(Path baseDir, String dirName) {
        File dirStatus = baseDir.toFile();
        if (dirStatus.isDirectory()) {
            Path subDir = baseDir.resolve(dirName);
            try {
                Files.createDirectory(subDir);
                System.out.println("Successfully created directory.");
            } catch (IOException e) {
                System.out.println("Error creating directory.");
                System.out.println(e);
            }
        } else {
            System.out.println("""
                    Target file is not a directory.
                    Cannot create directory at specified location""");
        }
    }

    /**
     * Deletes the target directory, gives option to empty directory at new location if not empty
     *
     * @param targetToDelete the path of the directory to delete
     */
    public static void deleteAFile(Path targetToDelete) {
        try {
            Scanner scanner = new Scanner(System.in);
            if (Files.isDirectory(targetToDelete)) {
                File targetDirectory = new File(targetToDelete.toUri());
                File[] directoryContent = targetDirectory.listFiles();
                if (directoryContent != null && directoryContent.length > 0) {
                    System.out.println("The directory is not empty.\n" + getPathInfo(targetToDelete)
                            + "\nWould you like to move the files to a new directory?\nYES\nNO");
                    String option = scanner.nextLine();
                    if (option.equalsIgnoreCase("yes")) {
                        System.out.println("What destination would you like to move the files to?");
                        String destinationPath = scanner.nextLine();
                        Path destination = Paths.get(destinationPath);
                        for (File file : directoryContent) {
                            moveFile(file.toPath(), destination);
                        }
                        Files.deleteIfExists(targetToDelete);
                        System.out.println("Successfully deleted directory.");
                    } else {
                        System.out.println("The directory is not empty and could not be deleted.");
                    }
                }
            } else {
                Files.deleteIfExists(targetToDelete);
                System.out.println("Successfully deleted file.");
            }
        } catch (IOException e) {
            System.out.println("Error deleting file.");
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    /**
     * Moves file to new directory, accounts for a file existing at destination
     *
     * @param source      the file to be copied over
     * @param destination the directory to be copied to
     */
    public static void moveFile(Path source, Path destination) {
        try {
            Scanner scanner = new Scanner(System.in);
            if (destination.toFile().isDirectory()) {
                if (!Files.exists(destination.resolve(source.getFileName()))) {
                    Files.move(source, destination.resolve(source.getFileName()));
                    System.out.println("File moved successfully.");
                } else {
                    System.out.println("""
                            File with that name already exists at destination.
                            Would you like to overwrite the existing file?
                            YES
                            NO""");
                    String option = scanner.nextLine();
                    if (option.equalsIgnoreCase("yes")) {
                        Files.move(source, destination.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File copied to " + destination.getFileName());
                    } else {
                        System.out.println("File not moved.");
                    }
                }
            } else {
                System.out.println("""
                        Destination file is not a directory.
                        Cannot move file to destination.""");
            }
        } catch (IOException e) {
            System.out.println("Error moving file.");
            System.out.println(e);
        }

    }

    /**
     * Creates a file of a specified name at the specified destination
     */
    public static void createFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the name of the file?");
        String fileName = scanner.nextLine();
        System.out.println("What is the destination path?");
        String destination = scanner.nextLine();
        Path newFile = Paths.get(destination, fileName);
        try {
            Files.createFile(newFile);
            System.out.println("Successfully created file.");
        } catch (IOException e) {
            System.out.println("Error creating file.");
            System.out.println(e);
        }
    }

    /**
     * Copies file to destination, gives options if file already exists
     * @param source file to be copied
     * @param destination target directory
     */
    public static void copyFile(Path source, Path destination){
        try {
            Scanner scanner = new Scanner(System.in);
            if(destination.toFile().isDirectory()){
                if(!Files.exists(destination.resolve(source.getFileName()))){
                    Files.copy(source, destination.resolve(source.getFileName()));
                    System.out.println("Filed copied to destination successfully");
                } else {
                    System.out.println("""
                File already exists at destination.
                Would you like to overwrite the existing file?
                YES
                NO""");
                    String option = scanner.nextLine();
                    if(option.equalsIgnoreCase("yes")){
                        Files.copy(source, destination.resolve(source.getFileName()),
                                StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File at destination replaced.");
                    } else {
                        System.out.println("""
                    File exists at destination.
                    Could not copy.""");
                    }
                }
            } else {
                System.out.println("""
                        Destination is not a directory." +
                        Cannot copy to destination""");
            }
        }catch (IOException e){
            System.out.println("Error copying file.");
            System.out.println(e);
        }
    }

    /**
     * Seaches for file by name
     * @param directory the directory to search
     * @param searchString the string to search by
     */
    public static void searchFiles(Path directory, String searchString){
        File[] files = directory.toFile().listFiles();
        if(files != null){
            for(File file: files){
                if(!file.isDirectory()){
                    String fileName = file.getName();
                    if(fileName.toLowerCase().contains(searchString.toLowerCase())){
                        System.out.println("Found file: " + fileName +
                                "\nat " + file.getPath());
                    }
                } else{
                    searchFiles(file.toPath(), searchString);
                }
            }
        }
    }
}