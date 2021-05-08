FROM postgres:13

RUN echo "wal_level = logical" >> /usr/share/postgresql/13/postgresql.conf.sample
COPY init.sql /docker-entrypoint-initdb.d/