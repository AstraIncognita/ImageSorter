package com.imagesorter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

public class Model {

    private File inputDirectory;
    private List<File> outputDirectoryList = new LinkedList<>();
    private List<File> fileList = new LinkedList<>();

    void setInputDirectory(File directory) throws IllegalArgumentException
    {
        if (directory.isDirectory())
        {
            inputDirectory = directory;
        }
        else
        {
            throw new IllegalArgumentException("File is not a directory.");
        }
    }

    boolean isInputDirectoryValid()
    {
        return (inputDirectory != null && inputDirectory.exists() && inputDirectory.isDirectory());
    }

    void addOutputDirectory(File directory) throws IllegalArgumentException
    {
        if (directory.isDirectory())
        {
            outputDirectoryList.add(directory);
        }
        else
        {
            throw new IllegalArgumentException("File is not a directory.");
        }
    }

    void removeOutputDirectory(int index) throws IndexOutOfBoundsException
    {
        if (index >= 0 && index < outputDirectoryList.size())
        {
            outputDirectoryList.remove(index);
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    int outputDirectoriesCount()
    {
        return outputDirectoryList.size();
    }

    void generateFileList()
    {
        String[] extensions = {"jpg", "jpeg", "bmp", "png"};

        FilenameFilter filter = (dir, name) -> {
            for(String extension : extensions)
            {
                if (name.endsWith("." + extension))
                {
                    return true;
                }
            }
            return false;
        };

        File[] imageArray = inputDirectory.listFiles(filter);
        if (imageArray != null) {
            fileList = new LinkedList<>(List.of(imageArray));
        }
        else
        {
            fileList = new LinkedList<>();
        }
    }

    void removeInvalidFiles()
    {
        fileList.removeIf(file -> !file.exists());
    }

    boolean hasFiles()
    {
        return fileList.size() > 0;
    }

    File getCurrentFile()
    {
        if (fileList.size() > 0 && fileList.get(0).exists())
        {
            return fileList.get(0);
        }
        else
        {
            skipCurrentFile();
            return null;
        }
    }

    void skipCurrentFile()
    {
        if (fileList.size() > 0)
        {
            fileList.remove(0);
        }
    }

    boolean tryMoveCurrentFile(String newFileName, int outputDirectoryIndex)
    {
        if (fileList.size() == 0)
        {
            return false;
        }

        String fileName = fileList.get(0).getName();
        String newPath =
                outputDirectoryList.get(outputDirectoryIndex).getAbsolutePath()
                        + "/"
                        + newFileName
                        + fileName.substring(fileName.lastIndexOf('.'));

        boolean isMoved = fileList.get(0).renameTo(new File(newPath));
        if (isMoved)
        {
            fileList.remove(0);
        }
        return isMoved;
    }
}
