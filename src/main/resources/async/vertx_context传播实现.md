## context传播

- VertxThread持有一个context对象
- future持有一个context对象，在调用future方法时，先设置当前VertxThread的context对象
- 执行回调函数时，设置当前thread的context对象
- SQL执行时，全部有context

