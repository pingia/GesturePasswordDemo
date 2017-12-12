GesturePasswordDemo
===============

this is a GesturePassword View component. you can use it to make user's login more convenient.

![GesturePasswordDemo](https://github.com/pingia/GesturePasswordDemo/raw/master/screenshots/gesture_login.png)

![GesturePasswordDemo](https://github.com/pingia/GesturePasswordDemo/raw/master/screenshots/gesture_set.png)

if you need gesture setting page,you need use  View class NineCircularGridLayout.java and NineCircularLittleGridLayout.java.
the NineCircularGridLayout will allow you typed the gesture, and the NineCircularLittleGridLayout will give you the feedback of the gesture you typed.

if you need gesture login page, you only need use View class NineCircularGridLayout.java.

you can redirect to the "app" directory of the demo project, to get more information about how to use the component more comfortably.

Gradle
------
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
	dependencies {
	        compile 'com.github.pingia:GesturePasswordDemo:1.0.0'
	}
```

Usage
-----
```xml
<com.pingia.cn.gesturepassword.lib.NineCircularLittleGridLayout
      android:id="@+id/nine_grid_little_layout"
      android:layout_width="80dp"
      android:layout_height="wrap_content"
      app:little_normalColor = "#aaaaaa" <!-- 指引图的默认圆圈颜色 -->
      app:little_fillColor = "#32e86b" <!-- 指引图的填充颜色 -->
      app:little_lineToWidth = "6dp" <!-- 指引图连线粗细 -->
      app:little_normalCircularLineWidth = "6dp" <!-- 指引图圆环描线粗细 -->
      app:little_LineToColor="#32e86b" <!--指引图连线颜色-->
/>

<com.pingia.cn.gesturepassword.lib.NineCircularGridLayout
      android:id="@+id/nine_grid_layout"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:layout_centerInParent="true"
      app:minFingerLineToNums = "4" <!-- 默认最小连接需要四个-->
      app:normalColor = "#cccccc" <!-- 手势图的初始圆圈颜色 -->
      app:fingerOnColor = "#32e86b" <!-- 手势按下去的时候圆圈颜色 -->
      app:fingerLineToColor = "#32e86b" <!-- 手势图的连线颜色 -->
      app:wrongLineToColor = "#ff0000" <!-- 手势图不符合要求时，错误连线颜色 -->
      app:normalCircularLineWidth="6dp" <!--手势图连线颜色-->
      app:fingerLineToWidth = "6dp" <!--手势图的连线粗细 -->
                                                         
    />
```
