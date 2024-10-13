this is a scrapper of merriamWebster dictionnary, this is just a hobby project of a student not intended for any commercial use.
the logic uses threads to make the process faster, the first scrapper of the words is fast, so we kept it in one thread 
to gather all definitions, we noticed the process takes too much time, which is why i created a process based on threads and splitting, first we split the main csv containing all the words into the number of threads user wants to use
each thread works by itself to extract definitions for the words they are provided from the split csv
the swing app is used to show the result

Note : 
  if software takes too much time, you can go stop it whenever you want, edit the csvs to delete all the incomplete enteries, then merge csvs from the merge class and start the GUI

  I couldve made it possible to pause and continue if wanted , but i just don't think i will learn much by doing that . 

  
