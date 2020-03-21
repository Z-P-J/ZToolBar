# ZToolBar
 A custom ToolBar for Android.

## Usage

Gradle

```java
implementation 'com.zpj.widget:ZToolBar:1.0.0'
```

ToolBar

```xml
<com.zpj.widget.toolbar.ZToolBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:z_toolbar_fillStatusBar="true"
        app:z_toolbar_statusBarColor="@color/colorPrimaryDark"
        app:z_toolbar_titleBarColor="@color/colorPrimary"
        app:z_toolbar_leftType="imageButton"
        app:z_toolbar_centerText="@string/app_name"
        app:z_toolbar_rightType="textView"
        app:z_toolbar_rightText="test"
        />
```

SearchBar

```xml
<com.zpj.widget.toolbar.ZSearchBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:z_toolbar_titleBarColor="@color/colorPrimary"
        />
```
