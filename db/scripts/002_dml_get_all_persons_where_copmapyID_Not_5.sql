select p.id as Person_id, p.name as Person_name, c.name as Company_name 
from person p  
join company c 
on p.company_id=c.id 
where c.id!=5;



