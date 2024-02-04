const User = require('./../Models/userModel');
const Post = require('./../Models/postModel')
const AppError = require('./../utils/appError');
const catchAsync = require('./../utils/catchAsync');
const multer = require('multer');

const multerStorage = multer.diskStorage({
    destination : (req,file,cb)=>{
        cb(null,'public/users')
    },
    filename: (req,file,cb)=>{
        const ext = file.mimetype.split('/')[0];
        cb(null `user-${req.params.id}-${Date.now()}.${ext}`);
    }
});

const multerFilter = (req,file,cb)=>{
  if(file.mimetype.startsWith('image')){
    cb(null,true);
  }else{
    cb(new AppError('Not an image ! Please upload an image',400),false);

  }
}
const upload = multer({
    storage : multerStorage,
    fileFilter : multerFilter
 });

exports.UploadUserPhoto = upload.single('photo')

exports.getUser = catchAsync(async (req,res,next)=>{

    const user = await User.findById(req.params.id);
    if(!user){
        next(new AppError('No User Found',404));
    }

    res.status(200).json({
        status: 'success',
        data : {
            user
        }
    })
});

exports.getPost = catchAsync(async (req,res,next)=>{
    const post = await  Post.find({ user : req.params.id }); 

    if(post.length == 0){
        return next(new AppError('You have not posted any thing',404));
    }

    res.status(200).json({
        status : 'success',
        data : {
            post
        }
    })
});
const filterObj = (obj,...rest)=>{
    const newObj={};
    // for looping object
    Object.keys(obj).forEach(el=>{
      if(rest.includes(el)) newObj[el]=obj[el];
    })
    return newObj;
  }

exports.updateOne = catchAsync(async (req,res,next)=>{
    
    const filterBody = filterObj(req.body,'supported','address');
    if(req.file) filterBody.photo = req.file.filename
    const updated = await User.findByIdAndUpdate(req.params.id,req.body,{ new : true,runValidators: true});

    if(!updated){
        return next(new AppError('No user found',404));
    }

    res.status(200).json({
        status : 'success',
        data : {
            user : updated
        }
    });
});