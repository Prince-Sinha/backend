import ModeEditIcon from '@mui/icons-material/ModeEdit';
import { buttonBaseClasses } from '@mui/material';
import { Form } from 'react-router-dom'
import {useState,useEffect} from 'react'
export default function Profile(){
    const [edit,setEdit]= useState(false);
    const [profileData,setData]= useState({
        name : '',
        email : '',
        phoneNumber : '',
        State: '',
        city : '',
        address : '',
    });
    const id = localStorage.getItem('_id');
    console.log(id);
   
    useEffect(()=>{
        async function fetchData(){
  
           try{
              const response = await fetch(`http://127.0.0.1:8000/api/v1/users/${id}`);
              const resData = await response.json();
  
              setData(resData.data.user);
              console.log(profileData);
  
           }catch(err){
               console.log(err);
           }
        }
        
        fetchData();
  
     },[]);

    return <main id="profile">
        <Form>
        <div className='profile-btn' >
          {!edit ? <a > <ModeEditIcon color='primary'/> </a>: <button type="submit" >Save</button>}
        </div>
        <div className="profile-name">
            <img src={`http://127.0.0.1:8000/users/user1.jpg`} alt="Profile" />
            <p>{profileData.name}</p>
        </div>

        {edit &&<input type='file' name='file' accept='image/*' className='file'/>}
         <div className="profile-info">
           <p className="p">Email:</p>
           <p className='detail'>{profileData.email}</p>

         </div>
         <div className="profile-info">
           <p className="p">Phone Number:</p>
           <p className='detail'>{profileData.phoneNumber}</p>
         </div>
         <div className="profile-info">
           <p className="p">State:</p>
           <p className='detail'>{profileData.State}</p>
         </div>
         <div className="profile-info">
           <p className="p">City:</p>
           <p className='detail'>{profileData.city}</p>
         </div>
         <div className="profile-info">
           <p className="p">Address:</p>
           <p className='detail'>{profileData.address}</p>
         </div>
        </Form>
    </main>
}