
import SignupSide from "./SignupSide.jsx";
import Signupform from "./Signupform.jsx";

export default function Signup(){
  
  return (
  <div className="container">
      <SignupSide />
      <div className="box box-content">
        <p >SignUp</p>
        <div className="form">
          <Signupform />
        </div>
      </div>
  </div>);
}

