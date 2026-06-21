#!/bin/bash
# Allows the replicator user to connect from any host for replication
echo "host replication replicator all md5" >> "$PGDATA/pg_hba.conf"
#pg_ctl reload -D "$PGDATA"