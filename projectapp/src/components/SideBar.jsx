import { NavLink , useRouteLoaderData} from 'react-router-dom'
import { useState } from 'react'
export default function SideBar(){
    const [activeClass , setActiveClass]= useState('unresolved');

    const handleChange = (name)=>{

        setActiveClass(name);
    }
    const token = useRouteLoaderData('root');
    return (
         
         <div className="side">
            {/* {console.log(activeClass)} */}
            <ul>
                <li className={activeClass==='article'?'act':undefined}><NavLink to='/article' onClick={() => handleChange('article')} end><button>Article</button></NavLink></li>
                <li className={activeClass === 'unresolved'?'act':undefined}><NavLink to='/' end><button>Unresolved</button></NavLink></li>
                <li className={activeClass==='resolved'?'act':undefined}><NavLink to="/resolved" onClick={()=>handleChange('resolved')} end><button>Resolved</button></NavLink></li>
                {token && <li className={activeClass==='post'?'act':undefined}><NavLink to="/posts" onClick={()=>handleChange('post')} end><button>Posts</button></NavLink></li>}
                {token && <li className={activeClass==='profile'?'act':undefined}><NavLink to="/profile" onClick={()=>handleChange('profile')} end><button>Profile</button></NavLink></li>}
            </ul>
         </div>
       
    )
}