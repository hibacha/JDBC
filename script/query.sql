select p.name as `Photographer`,stat.ptype as `Specialty`, stat.num as `Number of photos` from
(select p.`takenBy` as phid, p.`type` as ptype , count(*) as num from photo p where p.type is not null group by p.`takenBy`,p.`type` union
select p.`takenBy` , "TOTAL",  count(*) from photo p group by p.`takenBy` union 
select p.`takenBy` as phid, "No Type" , count(*) as num from photo p where p.type is null group by p.`takenBy`,p.`type`) as stat  
join person p on p.id=stat.phid order by Photographer, ptype
 
 
select loc.country, loc.state, loc.city, p.name 
from Person p join Photographer ph on p.id=ph.id 
join location loc on loc.id=ph.livesIn 
group by country,  state, city,p.name