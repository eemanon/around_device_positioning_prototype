# Magnetometer Prototype
A project aiming at identifying device position with centimeter precision. A magnet is used to create a magnetic field. The device's magnetometer is used to determine the device's position by magnetic fingerprinting.

## Code structure
All relevant code resides within the class "MainActivity.java" in app/src/main/java/com/example/magnetometerrawdata. "PrototypeActivity.java" allows for mapping a magnetic field from a custom magnet.



Currently developping a prototype to get positioning data on magnetometer manually on a centimeter grid and evaluating data. Requires a php/mysql server. php script provided.

Requires a database mysql named "situlearn" and a table "magnetometer" with the following columns (name type): 
(position_name 	text )
(x 	            float )
(y 	            float )
(z 	            float )
(realX 	        int(11))
(realY 	        int(11))
(mduration 	    int(11) )
(minterval    	  int(11) )
(time 	          bigint(20) 	)
