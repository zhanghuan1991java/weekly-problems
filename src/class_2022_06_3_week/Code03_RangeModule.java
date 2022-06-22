package class_2022_06_3_week;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// 测试连接 : https://leetcode.com/problems/range-module/
public class Code03_RangeModule {

	class RangeModule {
		// 某个区间的开头是key，结尾是value
		TreeMap<Integer, Integer> map;

		public RangeModule() {
			map = new TreeMap<>();
		}

		// 加入一个区间
		public void addRange(int left, int right) {
			// 无效区间直接返回
			if (right <= left) {
				return;
			}
			// 当前要加入的区间是[left, right)
			// 比如 : 当前区间[34, 76)
			// 第一个if：
			// 如果小于等于34的开头，和小于等于76的开头不存在
			// 那么说明，34之前没有区间，34到75也没有区间
			// 直接加入就可以了
			// 第二个if：
			// 说明有小于等于34的开头，并且延伸的结尾大于等于34
			// 比如，之前有两个区间[30, 54), [70, 108)
			// 有小于等于34的开头，就是30
			// 并且延伸的结尾大于等于34，就是54
			// 同时，看一下小于等于76的开头，就是70，延伸到108，所以一起合并
			// 合并成[30, 108]
			// 再比如，之前有两个区间[30, 54), [70, 72)
			// 有小于等于34的开头，就是30，并且延伸的结尾大于等于34，就是54
			// 同时，看一下小于等于76的开头，就是70，延伸到72，所以一起合并
			// 合并成[30, 76]
			// 即: map.put(start, Math.max(map.get(end), right));
			// 第三个分支，最后的else：
			// 说明是前两个if的反面：
			// 第一个if的反面: 小于等于34的开头，和小于等于76的开头，不是都不存在
			// 分成以下几种情况
			// 1) 小于等于34的开头存在，小于等于76的开头不存在，这是不可能的
			// 2) 小于等于34的开头不存在，小于等于76的开头存在，这是可能的
			// 3) 小于等于34的开头存在，小于等于76的开头也存在，这是可能的
			// 于是，第一个if的反面，分成了如下两种情况
			// a) 小于等于34的开头不存在，小于等于76的开头存在
			// b) 小于等于34的开头存在，小于等于76的开头也存在
			// 再看第二个if的反面
			// 第二个if是，小于等于34的开头存在，并且，结尾延伸到了34以右
			// 所以第二个if的反面是：
			// c) 小于等于34的开头不存在
			// d) 小于等于34的开头存在，但是结尾没有延伸到34
			// 那么a，b，c，d结合就有四种可能
			// 1) a + c，小于等于34的开头不存在，小于等于76的开头存在
			// 2) a + d，不可能
			// 3) b + c，不可能
			// 4) b + d，小于等于34的开头存在，但是结尾没有延伸到34，小于等于76的开头也存在
			// 只有1)、4）是可能的
			// 如果是1)，那么一定会新出现一个开头为34的区间，但是结尾在哪？
			// 比如当前区间[34, 76)，情况1)是：小于等于34的开头不存在，小于等于76的开头存在
			// 比如，之前有个区间是[56,72)，小于等于76的开头是56
			// 这两个区间合并成[34, 76)
			// 在比如，之前有个区间是[56,108)，小于等于76的开头是108
			// 这两个区间合并成[34, 108)
			// 即：map.put(left, Math.max(map.get(end), right));
			// 如果是4)，小于等于34的开头存在，但是结尾没有延伸到34，小于等于76的开头也存在
			// 这种情况下，虽然小于等于34的开头存在，
			// 但是结尾没有延伸到34，那么就可以不管之前的区间啊，反正不会有影响的。
			// 于是处理和情况1)是一样的
			// 即：map.put(left, Math.max(map.get(end), right));
			// 这就是接下来三个逻辑分支的处理
			Integer start = map.floorKey(left);
			Integer end = map.floorKey(right);
			if (start == null && end == null) {
				map.put(left, right);
			} else if (start != null && map.get(start) >= left) {
				map.put(start, Math.max(map.get(end), right));
			} else {
				map.put(left, Math.max(map.get(end), right));
			}
			// 上面做了合并，但是要注意可能要清理一些多余的区间
			// 比如，当前区间[34, 76)
			// 之前的区间是[100, 840)
			// 这种情况，中了分支一，在合并之后，区间为：
			// [34, 76)，[100, 840)
			// 所以移除掉所有(34,76]的开头
			// 只剩下了[34, 76)，[100, 840)
			// 再比如，当前区间[34, 76)
			// 之前的区间是[30, 54)、[55, 60)、[62, 65)、[70, 84)
			// 这种情况，中了分支二，在合并之后，区间为：
			// [30, 84)、[55, 60)、[62, 65)、[70, 84)
			// 所以移除掉所有(30,84]的开头区间
			// 只剩下了[30, 84)
			Map<Integer, Integer> subMap = map.subMap(left, false, right, true);
			Set<Integer> set = new HashSet<>(subMap.keySet());
			map.keySet().removeAll(set);
		}

		public boolean queryRange(int left, int right) {
			Integer start = map.floorKey(left);
			if (start == null)
				return false;
			return map.get(start) >= right;
		}

		public void removeRange(int left, int right) {
			if (right <= left)
				return;
			Integer start = map.floorKey(left);
			Integer end = map.floorKey(right);
			if (end != null && map.get(end) > right) {
				map.put(right, map.get(end));
			}
			if (start != null && map.get(start) > left) {
				map.put(start, left);
			}
			Map<Integer, Integer> subMap = map.subMap(left, true, right, false);
			Set<Integer> set = new HashSet<>(subMap.keySet());
			map.keySet().removeAll(set);
		}
	}

}
