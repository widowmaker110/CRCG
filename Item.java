package dakota.subsetsum;

import java.util.ArrayList;
import java.lang.String;

public class Item
{
	public String title;
	public int cost;
	public int maxSubsetSize = 1;
	
	public Item(String t, int c, int m)
	{
		title = t;
		cost = c;
		maxSubsetSize = m;
	}
	
	public Item(Item original)
	{
		this.title = original.title;
		this.cost = original.cost;
		this.maxSubsetSize = original.maxSubsetSize;
	}
}