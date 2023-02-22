select c.name, count(p.id) as count
from company c
join person p
on c.id = p.company_id
group by c.name
order by count desc