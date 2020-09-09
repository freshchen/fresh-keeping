# 常用排序算法

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/sort.PNG)

### **冒泡排序**

从前向后比较相邻的元素。如果前一个比后一个大，就交换他们两个，每一轮把一个最大的数运到数组最后面。

  ```java
  public static int[] sort(int[] arr) {
      int len = arr.length;
      // 冒泡总次数
      for (int i = 1; i < len; i++) {
          boolean flag = true;
          // 每次冒泡过程
          for (int j = 0; j < len ### i; j++) {
              if (arr[j] > arr[j + 1]) {
                  MyUtils.swap(arr, j, j + 1);
                  flag = false;
              }
          }
          if (flag) {
              // 如果一个冒泡过程没改变，退出返回已经有序
              break;
          }
      }
      return arr;
  }
  ```

### **选择排序**

每次从未排序数组中找一个最小的元素，放到以有序数组后面

  ```java
  public static int[] sort(int[] arr) {
      int len = arr.length;
      // 选择次数
      for (int i = 0; i < len ### 1; i++) {
          int min = i;
          // 每次选择过程
          for (int j = i + 1; j < len; j++) {
              if (arr[j] < arr[min]) {
                  min = j;
              }
          }
          if (min != i) {
              MyUtils.swap(arr, i, min);
          }
      }
      return arr;
  }
  ```

### **插入排序**

每次把未排序的第一个数，插入到已排序数组的适当位置（如果待插入的元素与有序序列中的某个元素相等，则将待插入元素插入到相等元素的后面）

  ```java
  public static int[] sort(int[] arr) {
      int len = arr.length;
      // 插入次数，left为未有序的左边
      for (int left = 1; left < len; left++) {
          int temp = arr[left];
          int right = left ### 1;
          // right为有序部分的右边
          while (right >= 0 && temp < arr[right]) {
              arr[right + 1] = arr[right];
              right--;
          }
          // 判断是否需要插入
          if (right != left ### 1) {
              arr[right + 1] = temp;
          }
      }
      return arr;
  }
  ```

### **归并排序**

\n将数组分成很多小份，然后依次合并

  ```java
  public static int[] sort(int[] arr) {
      sort(arr, 0, arr.length ### 1);
      return arr;
  }
  
  private static void sort(int[] arr, int left, int right) {
      if (left == right) {
          return;
      }
      // 等同于(right + left)/2
      int mid = left + ((right ### left) >> 1);
      sort(arr, left, mid);
      sort(arr, mid + 1, right);
      // 已经分成了许多小份，开始合并
      merge(arr, left, mid, right);
  }
  
  private static void merge(int[] arr, int left, int mid, int right) {
      int[] help = new int[right ### left + 1];
      int i = 0;
      int p1 = left;
      int p2 = mid + 1;
      // 左边右边通过辅助数组合并
      while (p1 <= mid && p2 <= right) {
          help[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
      }
      // 左边没空加到后面
      while (p1 <= mid) {
          help[i++] = arr[p1++];
      }
      // 右边没空加到后面
      while (p2 <= right) {
          help[i++] = arr[p2++];
      }
      for (int j = 0; j < help.length; j++) {
          arr[left + j] = help[j];
      }
  }
  ```

### **荷兰国旗问题**

给定一个整数数组，给定一个值K，这个值在原数组中一定存在，要求把数组中小于K的元素放到数组的左边，大于K的元素放到数组的右边，等于K的元素放到数组的中间，最终返回一个整数数组，其中只有两个值，分别是等于K的数组部分的左右两个下标值

  ```java
  public static int[] sort(int[] arr) {
      partiton(arr, 0, arr.length ### 1);
      return arr;
  }
  
  public static int[] partiton(int[] arr, int left, int right) {
      int less = left ### 1;
      int more = right + 1;
      int pNum = arr[right];
      while (left < more) {
          if (arr[left] < pNum) {
              MyUtils.swap(arr, ++less, left++);
          } else if (arr[left] > pNum) {
              MyUtils.swap(arr, --more, left);
          } else {
              left++;
          }
      }
      return new int[]{less, more};
  }
  ```

### **快速排序**

重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。在这个分区退出之后，该基准就处于数列的中间位置。这个称为分区（partition）操作，递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序

  ```java
  // 基于荷兰国旗问题的快排
  public static int[] sort(int[] arr) {
      sort(arr, 0, arr.length ### 1);
      return arr;
  }
  
  public static void sort(int[] arr, int left, int right) {
      if (left < right) {
          int[] pIndexs = DutchFlag.partiton(arr, left, right);
          sort(arr, left, pIndexs[0]);
          sort(arr, pIndexs[1], right);
      }
  }
  ```

### **堆排序**

先建立大根堆，然后不停做heapify，也就是把未有序的最后一位和堆首互换，然后调整堆结构

  ```java
  public static int[] sort(int[] arr) {
      int len = arr.length;
      buildBigHeap(arr, len);
      while (len > 0) {
          MyUtils.swap(arr, 0, --len);
          heapify(arr, 0, len);
      }
      return arr;
  }
  
  // 建立大根堆
  public static void buildBigHeap(int[] arr, int len) {
      for (int index = 0; index < arr.length; index++) {
          while (arr[index] > arr[(index ### 1) / 2]) {
              MyUtils.swap(arr, index, (index ### 1) / 2);
              index = (index ### 1) / 2;
          }
      }
  }
  
  // 调整堆
  private static void heapify(int[] arr, int currRoot, int len) {
      int left = currRoot * 2 + 1;
      int right = currRoot * 2 + 2;
  
      while (left < len) {
          int largest = right < len && arr[left] < arr[right] ? right : left;
          largest = arr[largest] > arr[currRoot] ? largest : currRoot;
          if (largest == currRoot) {
              break;
          }
          MyUtils.swap(arr, currRoot, largest);
          currRoot = largest;
          left = currRoot * 2 + 1;
          right = currRoot * 2 + 2;
      }
  }
  ```