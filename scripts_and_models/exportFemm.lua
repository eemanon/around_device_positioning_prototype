--bf_output.lua--
x1=-11.00
x2=23
dx=0.1
 
y1=-11.0
y2=19.00
dy=0.1
 
ni = (x2-x1)/dx+2
if(ni<0) then
  ni = ni*-1
end
nj = (y2-y1)/dy+2
if(nj<0) then
  nj = nj*-1
end
 
handle=openfile("output.csv","w")
write(handle,"x,y,B,Bx,By\n")
 
for j=0,nj-1,1 do
  for i=0,ni-1,1 do
  x=x1+i*dx
  y=y1+j*dy
  A,B1,B2=mo_getpointvalues(x,y)
  if (B1 ~= nil and B2 ~= nil) then
    write(handle,x,",",y,",",sqrt(B1*B1+B2*B2),",",B1,",",B2,"\n")
  end
 end
end
 
closefile(handle)