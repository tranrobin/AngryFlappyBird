# AngryFlappyBird
Implementation of the game Flappy Bird and Angry Bird using JavaFX



https://github.com/tranrobin/AngryFlappyBird/assets/102487760/2b907724-9b1e-4927-9bcd-014f4264c31e



Setup
---------------------------
1. Install Java version 11 or higher.

2. Install JavaFX. 
  * Navigate to https://gluonhq.com/products/javafx/ 
  * Download the compatible version of JavaFX SDK with your Java version. 
  * Unzip the folder and remember its location for later use.
  * If you're using a MAC, follow these extra steps to make sure the computer recognizes the libraries:
    * Navigate to the folder where JavaFX was unzipped. 
    * Go to lib, for each .dylib file 
    * Right-click > Open-with > Terminal > Open and then, close the terminal

3. Download Eclipse.
  * Navigate to the download website: https://www.eclipse.org/downloads/
  * Eclipse Configurations
    * Binding with JAVA:
      * MAC users: Click on Eclipse > Preferences
      * Window users:  Click on Window > Preferences
      * Go to Java > Installed JREs > Choose the one you installed.
      * Execution Environment > JavaSE-11 & Check on compatible JRE
      * Go to Java > Compiler > Set the Compiler Compliance Level to 11 or higher
      * Apply & Close

    * Binding with JavaFX:
      * MAC users: Click on Eclipse > Preferences
      * Window users:  Click on Window > Preferences
      * Go to Java > BuildPath > User Libraries 
      * New > Name “JavaFX11” > Select it and “Add External Jars” > 
      * Find the location where JavaFX (slide 6) was downloaded and select all .jar files.

4. Clone the repository:

   ```bash
   $ git clone https://github.com/MHC-SP23-CS225/angryflappybird-teampocky.git
   ```
   or download as zip and extract.
   
5. Open the repository in Eclipse.
  * Click on File > Import > Projects from Git > Existing Local Repository 
  * Add [Navigate to the local repository's location] > Select [Added repository] > Next > Finish
  * Make sure to follow the Eclipse Configurations to bind with Java and JavaFX in Step 3
  
  
  
How to play
---------------------------
* The player uses the button to control the Koya’s flight. The Koya is supposed to avoid all obstacles (including pipes, floors and carrots) while collecting as many avocados as possible. 
* If a carrot collects an avocado, 1 point will be lost. If the Koya collects a green avocado, 5 points will be added. If the Koya collects a golden avocado, it will go into autopilot mode and will not collide with any objects.
* If the Koya hits a pipe, 1 point will be lost. If the Koya hits the floor or a carrot, the game is over.
* There are 3 difficulty levels. The harder the game is, the more carrots will appear.


Contributors
---------------------------
* Professor Su (source codes)
* Robin Tran
* Jennifer Pham

Resources
---------------------------
Images and audios are collected from Google.
