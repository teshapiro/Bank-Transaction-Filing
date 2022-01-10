import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

public class BankTransactionFiling
{
	static String account = "Short Term Savings";

	public static void main(String[]args)
	{
		String[][] vendorData = getVendorData("Vendor Data.txt");
		String[] categories = new String[]{"Dining", "Groceries", "Health", "Gas and Auto", "Entertainment", "Transportation", "Other", "Income", "Unknown Category"};
		ArrayList<String[]> transactionData = new ArrayList<String[]>();

		//Read in transaction data and separate items in different columns
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(account+".csv")));

			//skips header lines
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();

			while(true)
			{
				String input = reader.readLine();
				if(input==null) break;

				if(account.equals("Checking") || account.equals("Short Term Savings"))
					transactionData.add(separateDataCheckingSavings(input));
				else if(account.equals("Credit Card"))
					transactionData.add(separateDataCreditCard(input));
				else
					System.out.println("Account doesn't exist!");
			}
			reader.close();
		}
		catch(IOException e)
		{
			System.out.println("I/O Exception: Can't find bank transaction file.");
		}

		//Categorizes the transactions and removes internal bank fund transfers
		for(int i=0;i<transactionData.size();i++)
		{
			if(transactionData.get(i)[5].equals("Deposit Home Banking Transfer"))
			{
				transactionData.remove(i);
				i--;
			}
			else
			{
				categorize(transactionData.get(i), vendorData);
			}
		}

		//Writes out reformatted transactions by category, including items that didn't get categorized
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("Reformatted Transactions "+account+".csv"));

			for(int i=0;i<categories.length;i++)
			{
				writer.write(categories[i]);
				writer.newLine();

				for(int j=0;j<transactionData.size();j++)
				{
					if(transactionData.get(j)[1].equals(categories[i]))
					{
						String output = "";
						if(account.equals("Checking") || account.equals("Short Term Savings"))
							output = transactionData.get(j)[2] + ", \"" + transactionData.get(j)[3] + "\", " + transactionData.get(j)[4] + ", \"" + transactionData.get(j)[5] + " / " + transactionData.get(j)[6] + "\", " + transactionData.get(j)[7];
						else if(account.equals("Credit Card"))
							output = transactionData.get(j)[2] + ", \"" + transactionData.get(j)[3] + "\", " + transactionData.get(j)[4] + ", \"" + transactionData.get(j)[5] + "\", " + transactionData.get(j)[6];
						else
							output = "Account doesn't exist!";
						writer.write(output, 0, output.length());
						writer.newLine();
					}
				}
			}
			writer.close();
		}
		catch(IOException e)
		{
			System.out.println("I/O Exception: Can't write formatted file.");
		}
	}

	//Pulls apart data in different columns and puts them into an array
	//Expects .csv file as formatted by SDFCU
	public static String[] separateDataCheckingSavings(String input)
	{
		int startIndex = 1;
		int endIndex = 2;
		while (input.charAt(endIndex) != '"') endIndex++;
		String transactionNumber = input.substring(startIndex, endIndex);

		startIndex = endIndex + 2;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != ',') endIndex++;
		String date = input.substring(startIndex, endIndex);

		startIndex = endIndex + 2;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != '"') endIndex++;
		String description = input.substring(startIndex, endIndex);
		String transactionType = description.substring(0,7).equals("Deposit") ? "Deposit" : "Withdrawal";

		startIndex = endIndex + 3;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != '"') endIndex++;
		String memo = input.substring(startIndex, endIndex);

		if (transactionType.equals("Deposit")) startIndex = endIndex + 3;
		else startIndex = endIndex + 2;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != ',') endIndex++;
		String amount = input.substring(startIndex, endIndex);

		return new String[]{transactionType, "Unknown Category", date, "Unknown Payee", "", description, memo, amount};
	}

	public static String[] separateDataCreditCard(String input)
	{
		//System.out.println(input);

		int startIndex = 1;
		int endIndex = 2;
		while (input.charAt(endIndex) != '"') endIndex++;
		String transactionNumber = input.substring(startIndex, endIndex);

		//System.out.println(transactionNumber);

		startIndex = endIndex + 2;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != ',') endIndex++;
		String date = input.substring(startIndex, endIndex);

		//System.out.println(date);

		startIndex = endIndex + 2;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != '"') endIndex++;
		String memo = input.substring(startIndex, endIndex);

		//System.out.println(memo);

		startIndex = endIndex + 3;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != '"') endIndex++;
		memo = memo + " " + input.substring(startIndex, endIndex);

		//System.out.println(memo);

		startIndex = endIndex + 2;
		endIndex = startIndex + 1;
		while (input.charAt(endIndex) != ',') endIndex++;
		String amount = input.substring(startIndex, endIndex);

		//System.out.println(amount);

		return new String[]{"Withdrawal", "Unknown Category", date, "Unknown Payee", "", memo, amount};
	}

	//Categorizes transactions and fills in more data
	public static void categorize (String[] input, String[][] vendorData)
	{
		for(int i=0;i<vendorData.length;i++)
		{
			if((account.equals("Checking") || account.equals("Short Term Savings")) ? input[6].contains(vendorData[i][0]) : input[5].contains(vendorData[i][0]))
			{
				input[1] = vendorData[i][1];
				input[3] = vendorData[i][2];
				input[4] = vendorData[i][3];
			}
		}
	}

	//Creates a reference list for vendors and associated data with each one
	//Expects file to begin with number of vendors, each line must contain a search term, category, payee (vendor name), and description
	public static String[][] getVendorData(String fileName)
	{
		String[][] vendorData = null;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));

			vendorData = new String[Integer.parseInt(reader.readLine())][4];

			for(int i=0;i<vendorData.length;i++)
			{
				String input = reader.readLine();
				int startIndex = 0;
				int endIndex = -1;

				for(int j=0;j<4;j++)
				{
					startIndex = endIndex + 1;
					while(input.charAt(startIndex) != '"') startIndex++;
					startIndex++;
					endIndex = startIndex + 1;

					while(input.charAt(endIndex) != '"') endIndex++;
					vendorData[i][j] = input.substring(startIndex, endIndex);
				}
			}

		}
		catch(IOException e)
		{
			System.out.println("I/O Exception: Can't find vendor list.");
		}

		return vendorData;
	}
}
