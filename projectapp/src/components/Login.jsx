import {IconButton,OutlinedInput,InputLabel,InputAdornment,FormControl,TextField} from '@mui/material';
import {Visibility,VisibilityOff} from '@mui/icons-material';
import {useState} from 'react'
import { Form ,NavLink } from 'react-router-dom';

export default function Login(){
    const [showPassword, setShowPassword] = useState(false);
    const handleClickShowPassword = () => setShowPassword((show) => !show);
    const handleMouseDownPassword = (event) => {event.preventDefault(); };

    return <section id="login">
        <div className="login-logo">
             <img src="./../../3R.png" alt="Logo" />
        </div>
        <div className='login-form'>
           <Form method="post">
             <div>
             <TextField
                     label="Phone Number"
                     id="outlined-start-adornment"
                     InputProps={{
                        startAdornment: <InputAdornment position="start">+91 </InputAdornment>,
                     }}
                     type="number"
                     name="phoneNumber"
                     color='success'
              />
             </div>
             <div>
             <FormControl sx={{ marginTop: 1 }} fullWidth variant="outlined">
                  <InputLabel htmlFor="outlined-adornment-password" color='success'>Password</InputLabel>
                  <OutlinedInput 
                      name="password" 
                      id="outlined-adornment-password" 
                      type={showPassword ? 'text' : 'password'}
                      endAdornment={ <InputAdornment position="end">
                                          <IconButton 
                                             aria-label="toggle password visibility" 
                                             onClick={handleClickShowPassword} 
                                             onMouseDown={handleMouseDownPassword} 
                                             edge="end">
                                          {showPassword ? <VisibilityOff /> : <Visibility />}
                                          </IconButton>
                                      </InputAdornment> }
                      label=" Password"
                      color='success'
                  />
             </FormControl>
               
             </div>
             <button type="submit" className="btn">Login</button>
            </Form>
            <NavLink to="/signup">Create Account</NavLink>
            <div className="forgot-password">
                 <a href="">Forgot Password?</a>
            </div>
        </div>
       
    </section>
}