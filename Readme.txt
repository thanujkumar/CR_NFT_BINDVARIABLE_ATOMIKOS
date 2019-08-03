https://www.atomikos.com/bin/Documentation/ConfiguringOracle

if oracle is less than 11g or 10.2.0.4

grant select on sys.dba_pending_transactions to <user name>;
grant select on sys.pending_trans$ to <user name>;
grant select on sys.dba_2pc_pending to <user name>;
grant execute on sys.dbms_system to <user name>;

https://www.atomikos.com/Documentation/SpringIntegration#Setting_Atomikos_System_Properti

https://spring.io/blog/2011/08/15/configuring-spring-and-jta-without-full-java-ee/
