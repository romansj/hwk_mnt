insert into customer (name, identity_number)
values ('Anastasia Beaverhausen', '011190-12345')
on conflict do nothing;

insert into customer (name, identity_number)
values ('John Doe', '011293-12345')
on conflict do nothing;

insert into customer (name, identity_number)
values ('Jane Doe', '010186-12345')
on conflict do nothing;

insert into customer (name, identity_number)
values ('Lorem Ipsum', '281298-12345')
on conflict do nothing;

insert into customer (name, identity_number)
values ('Karen Walker', '321336-76543')
on conflict do nothing;


insert into account (account_currency, account_number, cust_id)
values ('EUR', 'LV10MINT0012345678910', 1)
on conflict do nothing;

insert into account (account_currency, account_number, cust_id)
values ('EUR', 'LV10MINT0012345678911', 1)
on conflict do nothing;

insert into account (account_currency, account_number, cust_id)
values ('USD', 'LV10MINT0012345678912', 1)
on conflict do nothing;


insert into account (account_currency, account_number, cust_id)
values ('EUR', 'LV10MINT0012345678915', 2)
on conflict do nothing;

insert into account (account_currency, account_number, cust_id)
values ('EUR', 'LV10MINT0012345678913', 3)
on conflict do nothing;


insert into account (account_currency, account_number, cust_id)
values ('DKK', 'LV10MINT0012345678000', 4)
on conflict do nothing;

insert into account (account_currency, account_number, cust_id)
values ('USD', 'LV15MINT0012345678888', 5)
on conflict do nothing;



insert into balance (amount, currency, account_account_number)
values (5000.25, 'EUR', 'LV10MINT0012345678910')
on conflict do nothing;

insert into balance (amount, currency, account_account_number)
values (2000, 'EUR', 'LV10MINT0012345678911')
on conflict do nothing;

insert into balance (amount, currency, account_account_number)
values (19000.90, 'USD', 'LV10MINT0012345678912')
on conflict do nothing;

insert into balance (amount, currency, account_account_number)
values (6000.25, 'EUR', 'LV10MINT0012345678915')
on conflict do nothing;

insert into balance (amount, currency, account_account_number)
values (238.25, 'EUR', 'LV10MINT0012345678913')
on conflict do nothing;

insert into balance (amount, currency, account_account_number)
values (60000, 'DKK', 'LV10MINT0012345678000')
on conflict do nothing;

insert into balance (amount, currency, account_account_number)
values (100000, 'USD', 'LV15MINT0012345678888')
on conflict do nothing;