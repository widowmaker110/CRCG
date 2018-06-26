package dakota.subsetsum;

import java.util.ArrayList;

public class Subset
{
	public ArrayList<Item> set = new ArrayList<Item>();
	public int sum;
	
	public Subset(ArrayList<Item> s, int t)
	{
		set = s;
		sum = t;
	}
	
	public Subset(Subset original)
	{
		this.sum = original.sum;
		for(Item i : original.set)
		{
			set.add(new Item(i));
		}
	}
}
