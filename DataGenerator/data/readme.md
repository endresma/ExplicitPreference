

This folder contains real world data crawled from different websites.

#zillow_data.txt (original file 6d-zillow-2245109.txt) 
Crawled from zillow.com. It contains data on real estates in the USA. 

The original format of the Zillow data set is
*ID bedrooms bathrooms livingArea lotArea yearBuilt taxValue*

Since all lattice Skyline algorithms can only handle low-cardinality domains we modified the data set as follows
 
* taxValue is not relevant for us
* livingArea and lotArea are in sqft. We converted them in m^2
* yearBuilt is given as year date. We set it to the age of the estate, e.g. 2014 - yearBuilt. 

Zillow size:  2236252
maxLevels[]:  10,10,36,45


#nba_data.txt (original file 5d-nba-17265.txt)

NBA size: 17265
maxLevels[]: 10,10,10,10,10 



#house_data.txt (original file 6d-hou-127931.txt)



#weather_data.txt 






