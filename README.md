
# Qupla plugin  
Intellij plugin for the Qupla language.  
  
The Qupla language is not completely defined yet, so consider this plugin as experimental.  
We recommend to use the plugin with IntelliJ IDE (it also works with Android Studio) (minimal build : 182.0)  
  
Keep in mind that Qupla language is evolving almost every day, we do our best to keep this plugin compatible with latest Qupla version.

(this project is a community project, not affiliated with Iota Foundation)

  ## Getting started
 
 - If you don't already have IntelliJ (or Android Studio) installed on your computer, you can download and install 
 IntelliJ Community from [Jetbrains website](https://www.jetbrains.com/idea/download/)
 - Start IntelliJ (or Android Studio) and install the Qupla Plugin ([details here](#installation))
 - After plugin installation it is recommended to restart IntelliJ
 - Checkout the qupla repository: https://github.com/iotaledger/qupla and open it as a gradle project.
 - Start exploring qupla, base library and samples can be found under src/main/resources
 - Learn more about features supported by the plugin [here](#features)
 
  ![Qupla Plugin in Action](https://github.com/ben-75/qupla-idea-plugin/blob/interpreter/doc/ready.png?raw=true)
  
  ## Installation
  
  The Qupla plugin can be installed manually, downloaded from jetbrain plugin repositories, or it can be build from sources.
  
   ### Manual Install    
    
   - Download the [latest release : qupla-language-support.jar](https://github.com/ben-75/qupla-idea-plugin/releases) 
   - Open Settings from the menu File (Ctrl+Alt+S)
   - Select "Plugins" on the left pane
   - Choose "Install from disk" and select the qupla-language-support.jar
   - Restart IntelliJ  
   
  ### Install from plugins repository
  
   - Open Settings from the menu File (Ctrl+Alt+S)
   - Select "Plugins" on the left pane
   - Search "Qupla" in the search box
   - Click "Search in repositories" and install `Qupla Plugin`
   - Restart IntelliJ

  ### Building from sources  
  
Building the plugin from sources requires to setup an intellij-plugin-development-environment suitable for custom 
language plugin development. Instructions can be found 
[here](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html)  
  
Once your environment is ready, clone this repository, open the project and generate the parser and the lexer:  
  
 - To generate the parser : right-click on the file Qupla.bnf and select "Generate Parser Code"  
 - To generate the lexer : right-click on the file Qupla.flex and select "Run JFlex Generator"  
  
Now you should have a `gen/` directory at the root of the project with all the generated code.  
  
Make sure that the `gen/` directory is in the source path of your project   
(Open "Project Structure > Modules > qupla-language-plugin > tab "Sources")  
  
You can now run the plugin. If you plan to modify the bnf or the lexer: it can be useful to enable the plugin 
PsiViewer in the instance of IntelliJ running the plugin (If you followed carefully the instructions to 
setup your environment the PsiViewer is already enabled in your main instance of IntelliJ).

  ## Features
  
  - Syntax highlighting
  - Reference resolution (including cross-file, cross-module resolution) : ctrl-click on a symbol to see it's definition.
  - Rename symbols
  - Very basic code completion (but there is room for improvements)
  - Check validity of lut statements (no double entries)
  - Brace matching
  - Code folding
  - Integration with the Qupla interpreter through a specific "Run configuration" to easily execute Qupla code.
  - ...
  
  ### Integration with the Qupla interpreter
  
  The Qupla interpreter is developed by IOTA Foundation and is not part of this plugin. 
  Currently the plugin will search for the interpreter code in the current project, so to have the integration with the interpreter working as expected: the current project must be java based, and the interpreter code must be in the project.
  The interpreter code is available here https://github.com/iotaledger/qupla and if you follow the "Getting started" instructions : integration with interpreter should work just fine.
  
