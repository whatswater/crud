/**
 * @author whatswater
 * @implNote SQL操作的实现
 * 目前尚存在以下疑惑：
 * - 对SQL中各个位置能够出现的表达式或子句是否能够完全表示，类型的限制是否恰到好处（目前很多方法，都是返回具体的实现类，这样实现起来方便很多）
 * - 表达式的转换与生成SQL（最好在生成SQL前，进行一次转换，添加括号等，保证生成SQL时是上下文无关的）
 * - package的划分、各种类的命名
 * - Query和Table的界限
 * - 使用的便利性
 */
package com.whatswater.sql;
