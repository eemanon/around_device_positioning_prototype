# around_device_positioning_prototype
A project aiming at identifying device position with centimeter precision

Currently developping a prototype to get positioning data on magnetometer manually on a centimeter grid and evaluating data. Requires a php/mysql server. php script provided.

Requires a database mysql named "situlearn" and a table "magnetometer" with the following columns (name type): 
position_name 	text 
x 	            float 
y 	            float 
z 	            float 
realX 	        int(11)
realY 	        int(11)
mduration 	    int(11) 
minterval    	  int(11) 
time 	          bigint(20) 	
