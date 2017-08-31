//https://www.datastax.com/dev/blog/the-most-important-thing-to-know-in-cassandra-data-modeling-the-primary-key

CREATE TABLE michal_akka_eip.users(id text, name text, email text, PRIMARY Key(id)) ;
CREATE TABLE michal_akka_eip.userscc(ccid text, id text, name text, email text, PRIMARY Key(ccid, id)) ;
INSERT INTO michal_akka_eip.userscc(1,1,'michal','lolo@wp.pl');