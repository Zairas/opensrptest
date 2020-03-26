INSERT INTO report.unique_id(anm_id, last_value) VALUES ((SELECT ID FROM report.dim_anm WHERE anmidentifier='demo1'), 1000);

INSERT INTO report.unique_id(anm_id, last_value) VALUES ((SELECT ID FROM report.dim_anm WHERE anmidentifier='demo2'), 1100);

INSERT INTO report.unique_id(anm_id, last_value) VALUES ((SELECT ID FROM report.dim_anm WHERE anmidentifier='demo3'), 1200);

INSERT INTO report.unique_id(anm_id, last_value) VALUES ((SELECT ID FROM report.dim_anm WHERE anmidentifier='demo4'), 1300);

INSERT INTO report.unique_id(anm_id, last_value) VALUES ((SELECT ID FROM report.dim_anm WHERE anmidentifier='demo5'), 1400);
