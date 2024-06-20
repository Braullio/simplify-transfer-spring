DELETE FROM transactions;
DELETE FROM users;

INSERT INTO users ( id, user_type, full_name, tax_number, email, password_salt, password_hash, balance) VALUES(
             1,
             'COMUM',
             'Usuario Comum',
             '12345678901',
             'usuario.comum@example.com',
             'd057fc7347562abae79574604adfb2d729b1b59055c6b496a8079c7fc2ca59d7',
             '9753f72a13ce82702e16b55825a17f7cf92a2941bfa7f8c35871a423e35bb116',
             1000.00
         ),(
             2,
             'LOJISTA',
             'Usuario Lojista',
             '12345678000195',
             'usuario.lojista@example.com',
             '1a00e1a179373aa9b13d3e6765b809ec91a4d0cddf406214a144516466125011',
             '9b1f560dec75c06882073667ed0d760691fc4b03a0ad81415ec7315dbef82dc2',
             5000.00
         );