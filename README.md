[![](https://jitpack.io/v/STRENCH0/EasyDragAndDrop.svg)](https://jitpack.io/#STRENCH0/EasyDragAndDrop)
# Briefing
Do you want to implement In-App drag and drop in your android kotlin application? It's very simple! Here is 5 steps to do it using easy dnd:
1. Create class which will store all transfering data:
```kotlin
data class Assignment(override val tag: String) : DragAssignment
```
2. Create sets with sender and receiver objects:
```kotlin
val sendersSet = setOf(textView.assign(Assignment("textview1")))
val receiversSet = setOf(textView2.assign(Assignment("textview2")))
```
You can also use infix form of function!
```kotlin
textView2 assign Assignment("textview2")
```
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
4. Create set mappings:
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
    mapSets(sendersSet, receiversSet){
        onDragEntered {
            //some action when sender object covers receiver object
        }
    }
}
```
5. Enjoy it! You can also find kotlin docs [here](https://strench0.github.io/EasyDragAndDrop/).

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
        implementation 'com.github.STRENCH0:EasyDragAndDrop:1.1.1'
}
```
