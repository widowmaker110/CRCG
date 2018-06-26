/*****************************
Conditional Recursive Combination Generator
Author: Dakota Smith
Date: 06/26/2018
Version: 1.0
*****************************/

package dakota.subsetsum;

import java.util.ArrayList;
import java.lang.String;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class SubsetSum
{
	/*
		The following two functions, partition and sort, are
		a quicksort implementation grabbed from a google search,
		modified to sort an array of subsets rather than integers.
		https://www.geeksforgeeks.org/quick-sort/
	*/
	
	public static int partition(ArrayList<Subset> arr, int low, int high)
    {
        int pivot = arr.get(high).sum; 
        int i = (low-1);
        for (int j=low; j<high; j++)
        {
            if (arr.get(j).sum <= pivot)
            {
                i++;
 
                Subset temp = arr.get(i);
                arr.set(i,arr.get(j));
                arr.set(j,temp);
            }
        }
 
        Subset temp = arr.get(i+1);
        arr.set(i+1,arr.get(high));
        arr.set(high,temp);
 
        return i+1;
    }
 
    public static void sort(ArrayList<Subset> arr, int low, int high)
    {
        if (low < high)
        {
            int pi = partition(arr, low, high);
			
            sort(arr, low, pi-1);
            sort(arr, pi+1, high);
        }
    }
	
	/*
		calcMaxSubsetSizes determines for each item in the array,
		based on all the other items in the array, the size of
		the largest subset that each item can belong to by
		combining its cost with the costs of the cheapest items
		until the total goes over target
	*/
	
	public static void calcMaxSubsetSizes(ArrayList<Item> values, int target) //values must be sorted low to high
	{
		for(int i = 0; i < values.size(); i++)
		{
			int temp = values.get(i).cost;
			for(int j = 0; j < values.size(); j++)
			{
				if(!values.get(i).title.equals(values.get(j).title))
				{
					temp += values.get(j).cost;
					if(temp > target)
					{
						values.get(i).maxSubsetSize = j;
						break;
					}
				}
			}
		}
	}
	
	/*
		recCombGen is a conditional recursive combination generator.
		At each iteration of the function, subsets of size "size" are
		created by creating all the possible combinations of the items
		in the array and the subsets of "size"-1 returned by the
		recursive call, unless the combination would surpass the new
		item's max subset size or exceed the target value.
	*/
	
	public static ArrayList<Subset> recCombGen(int size, ArrayList<Item> values, int target)
	{
		ArrayList<Subset> subsets = new ArrayList<Subset>();
		int added = 0;
		if(size == 1)
		{
			for(int i = 0; i < values.size(); i++)
			{
				ArrayList<Item> temp = new ArrayList<Item>();
				temp.add(new Item(values.get(i)));
				Subset sub = new Subset(temp,values.get(i).cost);
				subsets.add(sub);
				added++;
			}
		}
		else
		{
			ArrayList<Subset> temp = recCombGen(size-1,values,target);
			subsets.addAll(temp);
			
			//the booleans in the following for loops are used for flow control:
			//each allows the inner three for loops to break to (and continue) the outermost for loop
			for(int i = 0; i < temp.size(); i++)
			{
				if(temp.get(i).set.size() < size-1) continue;
				boolean cont = false;
				for(int j = 0; j < values.size(); j++)
				{
					//these two outer for loops create all permutations
					
					if(values.get(j).maxSubsetSize < size) //eliminate maxSubsetSize violations
					{
						cont = true;
						break;
					}
					
					if(temp.get(i).sum + values.get(j).cost > target) //eliminate sums exceeding target
					{
						cont = true;
						break;
					}
					
					//these inner two for loops eliminate invalid new combinations (aabc)
					//and permutations that aren't combinations(bac when abc already exists)
					//by canceling any new subset that contains the current item (invalid combination)
					//or any previously iterated item (extra permutations)
					boolean cont2 = false;
					for(int k = 0; k < temp.get(i).set.size(); k++)
					{
						boolean cont3 = false;
						for(int l = 0; l <= j; l++)
						{
							if(temp.get(i).set.get(k).title.equals(values.get(j).title))
							{
								cont = true;
								cont2 = true;
								cont3 = true;
								break;
							}
						}
						if(cont3) break;
					}
					if(cont2) break;
					
					Subset tempS = new Subset(temp.get(i));
					tempS.set.add(new Item(values.get(j)));
					tempS.sum += values.get(j).cost;
					subsets.add(tempS);
					added++;
				}
				if(cont) continue;
			}
		}
		
		//remove combinations that can have at least one more item added to them without exceeding target.
		//test using the cheapest item not already in the subset.
		//in each iteration, perform this on subsets of size "size"-1, because the next iteration
		//of the function will need the subsets of size "size" to create the "size"+1 subsets.
		//the last iteration of subsets whose size is the max value of "size", do not need this
		//because it is impossible to have created a subset of max size that has room for another item
		//before hitting target, otherwise the maximum value would be greater
		int removed = 0;
		for(int i = 0; i < subsets.size(); i++)
		{
			if(subsets.get(i).set.size() != size-1) continue;
			for(int j = 0; j < values.size(); j++)
			{
				//booleans used for flow control. see above nested for loops for more detail
				boolean iCont = false;
				boolean jCont = false;
				for(int k = 0; k < subsets.get(i).set.size(); k++)
				{
					if(subsets.get(i).set.get(k).title.equals(values.get(j).title))
					{
						jCont = true;
						break;
						//if the subset already contains this item (beginning with the cheapest item), move to the next cheapest (continue j loop)
					}
				}
				if(jCont) continue;
				//found the cheapest item which is not in the subset
				if((subsets.get(i).sum + values.get(j).cost) <= target)
				{
					subsets.remove(i);
					i--;
					iCont = true;
					removed++;
					//subset is done, move to next subset (continue i loop)
				}
				if(iCont) break;
			}
		}
		
		System.out.println(size + "th iteration of recursive combination generator done!");
		System.out.println(added + " items added this iteration.");
		System.out.println(removed + " items removed this iteration.");
		System.out.println("Current subsets size: " + subsets.size() + "\n");
		return subsets;
	}
	
	/*
		main gathers the parameters and data for the problem,
		calculates values to be used in the generator function,
		runs the generator,
		sorts the results,
		and prints them to file.
	*/
	
	public static void main(String[] args)
	{
		//get target parameter and item data from input arguments
		if(args.length != 2)
		{
			System.out.println("Improper arguments. This program needs a target integer followed by a filename.");
			return;
		}
		int target = 0;
		try { target = Integer.valueOf(args[0]); }
		catch(NumberFormatException e)
		{
			System.out.println("First argument is not a valid integer.");
			return;
		}
		
		ArrayList<Item> values = new ArrayList<Item>();
		try
		{
			Scanner sc = new Scanner(new File(args[1]));
			while(sc.hasNext())
			{
				Item temp = new Item("",0,1);
				temp.title = sc.nextLine();
				temp.cost = sc.nextInt();
				if(sc.hasNext()) sc.nextLine();
				values.add(temp);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error in the second argument, noted by the type of the following exception:\n" + e.toString());
			return;
		}
		
		//perform maximum subset size calculations
		ArrayList<Subset> subsets = new ArrayList<Subset>();
		int maxSubsetSize = 1; //keep adding min elements to determine the largest subset possible without going bust over target
		int tempSum = 0;
		for(int i = 0; i < values.size(); i++)
		{
			tempSum += values.get(i).cost;
			if(tempSum > target)
			{
				maxSubsetSize = i; //would be i+1 but this iteration went bust so it isn't counted
				break;
			}
		}
		calcMaxSubsetSizes(values,target);
		System.out.println(maxSubsetSize + " iterations to execute!\n");
		
		//begin execution, and output execution time measurements as well as number of results
		long start = System.nanoTime();
		subsets.addAll(recCombGen(maxSubsetSize,values,target));
		long end = System.nanoTime();
		
		System.out.println("Total subsets size: " + subsets.size());
		System.out.print("Total recursive combination generator execution time (in seconds): ");
		System.out.print((end-start)/1000000000);
		System.out.print(".");
		System.out.println((end-start)-((end-start)/1000000000));
		
		//sort the results
		System.out.println("Sorting...");
		sort(subsets,0,subsets.size()-1);
		
		//send results to file
		System.out.println("Sorted. Sending to file...");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("results.txt")));
			for(int i = 0; i < subsets.size(); i++)
			{
				writer.write("Games: ");
				for(int j = 0; j < subsets.get(i).set.size(); j++)
				{
					writer.write(subsets.get(i).set.get(j).title);
					writer.write(", ");
				}
				writer.write("\n");
				writer.write("Sum: ");
				writer.write(String.valueOf(subsets.get(i).sum));
				writer.write("\n");
			}
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in writing the results to file, noted by the type of the following exception:\n" + e.toString());
			return;
		}
		
		System.out.println("A complete set of results has been sent to file results.txt");
	}
}