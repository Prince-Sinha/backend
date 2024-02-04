const express = require('express');
const postController = require('./../controllers/postController')
const router = express.Router();


router
   .route('/unresolved')
   .get(postController.unresolvedPost)
   .post(postController.createPost)
   .delete(postController.deletePost)

router.get('/resolved', postController.resolvedPost)

router
  .route('/:id')
  .get(postController.getonePost)
  .patch(postController.updatePost)

router
    .route('/create')
    .post(postController.createPost)
router.patch('/create/photo',postController.uploadPhoto,postController.resizePhoto,postController.fileUpdate)



module.exports = router; 