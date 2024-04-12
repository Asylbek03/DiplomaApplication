//package com.example.diplomaapplication.views.medicines
//
//import android.content.ContentValues.TAG
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.example.diplomaapplication.databinding.FragmentCameraBinding
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//class CameraFragment : Fragment() {
//
//    private var _binding: FragmentCameraBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var cameraExecutor: ExecutorService
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentCameraBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        startCamera()
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//                }
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//
//                cameraProvider.bindToLifecycle(
//                    viewLifecycleOwner, cameraSelector, preview
//                )
//
//                val imageAnalysis = ImageAnalysis.Builder()
//                    .build()
//                    .also {
//                        it.setAnalyzer(cameraExecutor, TextAnalyzer { text ->
//                            Log.d(TAG, "Detected text: $text")
//                            // After text recognition, pass it back to AddMedicineFragment
//                            val result = Bundle().apply {
//                                putString("scannedText", text)
//                            }
//                            parentFragmentManager.setFragmentResult("cameraFragmentResult", result)
//                            parentFragmentManager.popBackStack()
//                        })
//                    }
//
//                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, imageAnalysis, preview)
//
//            } catch (exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//        cameraExecutor.shutdown()
//    }
//
//    companion object {
//        private const val TAG = "CameraFragment"
//    }
//}
//
//// TextAnalyzer class
//class TextAnalyzer(private val listener: (String) -> Unit) : ImageAnalysis.Analyzer {
//    private val recognizer = TextRecognition.getClient()
//
//    override fun analyze(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        if (mediaImage != null) {
//            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//            recognizer.process(image)
//                .addOnSuccessListener { visionText ->
//                    val resultText = visionText.text
//                    listener.invoke(resultText)
//                }
//                .addOnFailureListener { e ->
//                    Log.e(TAG, "Text recognition failed", e)
//                }
//                .addOnCompleteListener {
//                    imageProxy.close()
//                }
//        }
//    }
//}
//
////class CameraFragment : Fragment(R.layout.fragment_camera) {
////    private lateinit var buttonCapture: Button
////    private lateinit var buttonCopy: Button
////    private lateinit var textViewData: TextView
////    private lateinit var bitmap: Bitmap
////
////    companion object {
////        private const val REQUEST_CAMERA_CODE = 100
////    }
////
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////        buttonCapture = view.findViewById(R.id.button_capture)
////        buttonCopy = view.findViewById(R.id.button_copy)
////        textViewData = view.findViewById(R.id.text_data)
////
////        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_CODE)
////        }
////
////        buttonCapture.setOnClickListener {
////            startCrop()
////        }
////
////        buttonCopy.setOnClickListener {
////            val scannedText = textViewData.text.toString()
////            copyToClipboard(scannedText)
////        }
////    }
////
////    private fun startCrop() {
////        cropImage.launch(
////            options {
////                setGuidelines(CropImageView.Guidelines.ON)
////            }
////        )
////    }
////
////    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
////        if (result.isSuccessful) {
////            val uriContent = result.uriContent
////            // Do something with the cropped image URI
////            try {
////                bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uriContent)
////                getTextFromImage(bitmap)
////            } catch (e: Exception) {
////                e.printStackTrace()
////            }
////        } else {
////            val exception = result.error
////            // Handle error
////        }
////    }
////
////    private fun getTextFromImage(bitmap: Bitmap) {
////        val recognizer = TextRecognizer.Builder(requireContext()).build()
////        if (!recognizer.isOperational) {
////            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
////        } else {
////            val frame = Frame.Builder().setBitmap(bitmap).build()
////            val textBlockSparseArray = recognizer.detect(frame)
////            val stringBuilder = StringBuilder()
////            for (i in 0 until textBlockSparseArray.size()) {
////                val textBlock = textBlockSparseArray.valueAt(i)
////                stringBuilder.append(textBlock.value)
////                stringBuilder.append("\n")
////            }
////            textViewData.text = stringBuilder.toString()
////            buttonCapture.text = "Переснять"
////            buttonCopy.visibility = View.VISIBLE
////        }
////    }
////
////    private fun copyToClipboard(text: String) {
////        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
////        val clip = ClipData.newPlainText("Скопированный текст", text)
////        clipboard.setPrimaryClip(clip)
////        Toast.makeText(requireContext(), "Скопировано", Toast.LENGTH_SHORT).show()
////    }
////}
//
//
////class CameraFragment: Fragment() {
////    Button button_capture, button_copy;
////    TextView textview_data;
////    private static final int REQUEST_CAMERA_CODE = 100;
////    Bitmap bitmap;
////    @Override
////    protected void onCreate(Bundle savedInstanceState){
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.fragment_camera);
////
////        button_capture = findViewById(R.id.button_capture);
////        button_copy = findViewById(R.id.button_copy);
////        textview_data = findViewById(R.id.textview_data);
////
////        if(ContextCompat.checkSelfPermission(CameraFragment.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
////            ActivityCompat.requestPermissions(CameraFragment.this, new String[]{
////                Manifest.permission.CAMERA
////            }, REQUEST_CAMERA_CODE);
////    }
////
////
////    button_capture.setonclicklistener(new View.onclicklistener(){
////        @override
////        public void onClick(View v){
////            CropImage.activity().setguidelines(CropImageView.guidelines.ON).start(CameraFragment.this)
////        }
////    })
////    button_copy.setonclicklistener(new View.onclicklistener(){
////        @override
////        public void onClick(View v){
////            String scanned_text = textview_data.getText().toString()
////            copyToClipBoard(scanned_text)
////        }
////    })
////
////}
////
////@override
////protected void onActivityResult (int requestCode, @Nullable Intent data)
////super.onActivityResult(requestCode, resultCode, data);
////if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
////    CropImage.ActivityResult result = CropImage . getActivityResult (data)
////    if (resultCode == RESULT_OK) {
////        Uri resultUri = result . getUri ();
////        try {
////            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
////            getTextFromOmage(bitmap)
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////    }
////
////    private void getTextFromImage(Bitmap bitmap){
////        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
////        if(!recognizer.isOperational()){
////            Toast.makeText(CameraFragment.this, "Error", Toast.LENGTH_SHORT).show()
////        }
////        else{
////            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
////            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
////            StringBuilder stringBuilder = new StringBuilder();
////            for(int i = 0; i <textBlockSparseArray.size(); i++){
////                TextBlock textBlock = textBlockSparseArray.valueAt(i);
////                stringBuilder.append(textBlock.getValue());
////                stringBuilder.append("\n");
////            }
////            text_data.setText(stringBuilder.toString())
////            button_capture.setText("Переснять")
////            button_copy.setVisibility(View.Visible);
////        }
////    }
////
////    private void copyToClipboard(String text){
////        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE)
////        ClipData clip = ClipData.newPlainText("Скопированный текст", text)
////        clipBoard.setPrimaryClip(clip)
////        Toast.makeText(CameraFragment.this,"Скопировано",Toast.LENGTH_SHORT).show()
////    }
////}
//
