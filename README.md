[![](https://jitpack.io/v/STRENCH0/EasyDragAndDrop.svg)](https://jitpack.io/#STRENCH0/EasyDragAndDrop)
# Briefing
Do you want to implement In-App drag and drop in your android kotlin application? It's very simple! Here is 5 (or less) steps to do it with DSL using easy dnd (if you don't like DSL skip it):
1. Create class which will store all transfering data or use some kotlin type:
```kotlin
data class Assignment(val tag: String)
```
2. Create sets with sender and receiver objects:
```kotlin
val sendersSet = setOf(textView.assign(Assignment("textview")))
val receiversSet = setOf(textView2.assign(Assignment("textview2")))
```
You can also use infix form of function!
```kotlin
textView2 assign Assignment("textview2")
```
Or you can do it latter (see point 4b).

3. Use enableDragAndDrop function and define callbacks using default (optional):
```kotlin
enableDragAndDrop<Assignment, Assignment> {
    default {
        onDropped { sender, receiver ->
            Toast.makeText(applicationContext, "Dropped sender on receiver", Toast.LENGTH_SHORT).show()
        }
    }
}
```
4a. Create set mappings:
```kotlin
enableDragAndDrop<Assignment, Assignment> {
    ...
    mapSets(sendersSet, receiversSet)
}
```
You can create as many mappings as you want. If some mapping must have unique behaviour just write it and it will override default methods:
```kotlin
enableDragAndDrop<Assignment, Assignment> {
    ...
    mapSets(sendersSet, receiversSet) {
        onDragEntered {
            //some action when sender object covers receiver object
        }
    }
}
```
4b. There is also a new way to create mappings. You can define mapping right here in the DSL!
```kotlin
mapSets {
    textView assignSender Assignment("textview")
    textView2 assignReceiver Assignment("textview2")
    config {
        onDropped { ... }
        onDragEntered { ... }
        onDragExited { ... }
    }
}
```
5. Enjoy it! You can also find kotlin docs [here](https://strench0.github.io/EasyDragAndDrop/).

# Classic way to configure
To configure drag-and-drop do next steps:
1. Create instance of DragAndDropManager class.
2. Init default config.
3. Create sets and optionally LocalConfig for each (see point 2 of previous topic).
4. Use mapSets method for all desired mappings.
5. !IMPORTANT! Use applyDragAndDrop() method.

# Installation
1. Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
2. Add the dependency
```groovy
dependencies {
        implementation 'com.github.STRENCH0:EasyDragAndDrop:2.0.1'
}
```
