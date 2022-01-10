# Bank-Transaction-Filing
This is a program I use at the beginning of every month to organize and file my bank statements.

I download a CSV file from my bank's website, put it in the same folder as this program, rename it, then run the batch file in the folder. The program uses a separate text file called "Vendor Data" that helps it categorize transactions based on what business they are with. Lines in "Vendor Data" are formatted like this:

"LEE'S BAKERY", "Dining", "Lee's Bakery", "Bahn Mi sandwhiches"

The first part is a recognizable part of the transaction details that the program looks for. The second part is the category ("Dining", "Entertainment", "Health", etc). The third part is just the business name formatted to be readable. The last part is a description of what the transaction most likely was. If the program can't find a matching line in "Vendor Data", it just sticks them in an "Unknown" category. I can add that unknown business into "Vendor Data" manually if I deem it likely for me to patronize them often. The output of the program is also a CSV file that I can then import into spreadsheet software, formatting my transactions how I like them.

Some potential additions to the program in the future:\
1.) Take in account name as an argument, rather than manually editing the .java file for each account CSV.\
2.) Have the program automatically add new data to "Vendor Data" for new businesses. This would likely require the program to access the internet to search around for info about the business.
