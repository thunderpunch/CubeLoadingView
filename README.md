# CubeLoadingView

An animated cubes loading progress bar for android

effect is shown as below

![](/gif/loading.gif)

## Usage

- add in your layout xml

  the animated content will be at the center with the given width and height.

```xml
  <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.thunderpunch.lib.CubeLoadingView
        android:id="@+id/clv"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        />
  </FrameLayout>
```

- by default, CubeLoadingView will start animation automatically. You could use ``stop()`` to stop current animation and ``start() ``to restart.

## XML attributes

| name         | format  | description                              |
| ------------ | ------- | ---------------------------------------- |
| mainColor    | color   | custom main color  (default #d77900)     |
| ceilColor    | color   | custom ceil color  (default #ffe33c)     |
| shadowColor  | color   | custom shadow color  (default #dbdbdb)   |
| shadowEnable | boolean | whether to display shadow or not (default true) |
| duration     | integer | the milliseconds time a cube takes to complete one animation cycle |


<img src = "/gif/attrs.gif" width = "500" height = "209"/>

  
  
## License

```
MIT License

Copyright (c) 2017 thunderpunch

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

