--
--  The MIT License (MIT)
--
--  Copyright (c) 2017 Bernardo Martínez Garrido
--  
--  Permission is hereby granted, free of charge, to any person obtaining a copy
--  of this software and associated documentation files (the "Software"), to deal
--  in the Software without restriction, including without limitation the rights
--  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
--  copies of the Software, and to permit persons to whom the Software is
--  furnished to do so, subject to the following conditions:
--  
--  The above copyright notice and this permission notice shall be included in all
--  copies or substantial portions of the Software.
--  
--  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
--  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
--  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
--  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
--  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
--  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
--  SOFTWARE.
--


-- ****************************************
-- This SQL script populates the initial data.
-- ****************************************

INSERT INTO example_entities (name) VALUES
   ('entity_01'),
   ('entity_02'),
   ('entity_03'),
   ('entity_04'),
   ('entity_05'),
   ('entity_06'),
   ('entity_07'),
   ('entity_08'),
   ('entity_09'),
   ('entity_10'),
   ('entity_11'),
   ('entity_12'),
   ('entity_13'),
   ('entity_14'),
   ('entity_15'),
   ('entity_16'),
   ('entity_17'),
   ('entity_18'),
   ('entity_19'),
   ('entity_20'),
   ('entity_21'),
   ('entity_22'),
   ('entity_23'),
   ('entity_24'),
   ('entity_25'),
   ('entity_26'),
   ('entity_27'),
   ('entity_28'),
   ('entity_29'),
   ('entity_30');
   
   INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (1,'admin','Admin','Admin','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','admin@admin.com','PUBLIC');
   	
   INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (2,'user1','User','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','user1@user.com','PUBLIC');
   	
   	INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (3,'private','User','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','private@user.com','PRIVATE');
   	
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (4,'mastodonte','Mastodonte','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','mastodonte@user.com','PUBLIC');
   	
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (5,'megalodon','Megalodón','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','megalodon@user.com','PUBLIC');
   	
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (6,'maquina','Máquina','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','maquina@user.com','PUBLIC');
   	
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (7,'pimbatumba','PimbaTumba','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','pimbatumba@user.com','PUBLIC');
   	
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (8,'gigantosaurio','Gigantosaurio','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','gigantosaurio@user.com','PUBLIC');
   		
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (9,'nasa','NASA','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','nasa@user.com','PUBLIC');
   	
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (10,'muydificil','MuyDifícil','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','muydificil@user.com','PUBLIC');
 
    INSERT INTO UserProfile (user_id,login,firstName,lastName,password,email,userType) 
   	VALUES (11,'crack','Crack','1','$2a$10$vSoD57cWmHKV1I0oE75djeemKJknUfiuNvL9NoRMhhy7tvjSg4euK','crack@user.com','PUBLIC');
 
   	
   INSERT INTO Follow (follow_id,user,followed_user,pending) 
   	VALUES (1,2,1,false);
   	   	
   INSERT INTO Picture (picture_id,description,image_path,date,user_id) VALUES (1,'asdfgasdfghg #hashtag1','null',now(),1);
   INSERT INTO Tag (tag_id,text) VALUES (1,'hashtag1');
   INSERT INTO PictureTag (picture_tag_id,tag,picture) VALUES (1,1,1);   
   
   INSERT INTO Post(post_id,date,user_id,picture_id,views,number_of_likes,anonymousViews,reshare) VALUES (1,now(),1,1,0,0,0,0);
   
