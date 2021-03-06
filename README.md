# ImageSorter
ImageSorter is a program which helps to sort large amounts of images from one input folder to multiple output folders.
It is written in Java using JavaFX, so it should be theoretically cross-platform, although it was only tested in Windows.

# Installation
ImageSorter comes in one .jar file and thus does not require installation. It can be downloaded at the Releases page. There are two versions: cross-platform one, which requires JavaFX runtime components, and a standalone fat Jar, which is built using a workaround and is expected to work only in Windows. Both versions require Java 17 to run.

# Usage
Waiting state of a program. You can select one input directory, from which images will be moved, and multiple output directories, in which images will be moved. Wrongly selected output directories can be removed from list. Sorting will begin after pressing Start button .
![image](https://user-images.githubusercontent.com/48684018/153099859-6a0f2b4f-80f3-4ae7-a8c3-5066c8d58b04.png)

Sorting state of a program. After seeing a picture, you can decide in which directory to move it. At the bottom, you can change the name of the image or skip the image entirely. In this case it will remain in input directory. Also, you can stop sorting to reconfigure input and output directories.
![image](https://user-images.githubusercontent.com/48684018/153100212-3be870b9-44a9-44dc-934c-f5049a064aae.png)
