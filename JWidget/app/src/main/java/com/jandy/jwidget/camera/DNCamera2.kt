package com.benew.ntt.jreading.arch.widget.vtstory

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.ntt.core.nlogger.NLogger
import com.visiontalk.vtbrsdk.base.AbstractVTCameraCtrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Arrays
import java.util.Collections

class DNCamera2 {

	var mContext : Context? = null

	constructor(context : Context) {
		mContext = context
	}

	private val TAG = DNCamera2::class.java.simpleName

	/**
	 * 照相机ID，标识前置后置
	 */
	private var mCameraId : String? = null

	/**
	 * 图像读取者
	 */
	private var mImageReader : ImageReader? = null

	/**
	 * 图像主线程Handler
	 */
	private var mCameraHandler : Handler? = null

	/**
	 * 相机设备
	 */
	private var mCameraDevice : CameraDevice? = null

	/**
	 * 预览大小
	 */
	private var mPreviewSize : Size? = null

	/**
	 * 相机请求
	 */
	private var mCameraCaptureBuilder : CaptureRequest.Builder? = null

	/**
	 * 相机拍照捕获会话
	 */
	private var mCameraCaptureSession : CameraCaptureSession? = null

	/**
	 * 相机管理者
	 */
	private var mCameraManager : CameraManager? = null
	private var mICameraPreviewCb : AbstractVTCameraCtrl.ICameraPreviewCallback? = null
	private var mOutCb : ICamera2PreviewCallback? = null
	private var mSurfaceTexture : SurfaceTexture? = null

	/**
	 * 相机设备状态回调
	 */
	private val mStateCallback : CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
		override fun onOpened(camera : CameraDevice) {
			// 打开
			mCameraDevice = camera
			// 开始预览
			takePreview()
		}

		override fun onDisconnected(camera : CameraDevice) {
			// 断开连接
			camera.close()
			mCameraDevice = null
		}

		override fun onError(camera : CameraDevice, error : Int) {
			// 异常
			camera.close()
			mCameraDevice = null
		}
	}

	fun openCamera(cameraId : Int, previewWidth : Int, previewHeight : Int, previewCallback : AbstractVTCameraCtrl.ICameraPreviewCallback?, surfaceHolder : SurfaceTexture) {
		mICameraPreviewCb = previewCallback
		mSurfaceTexture = surfaceHolder
		setUpCamera(cameraId, previewWidth, previewHeight)
		openCamera()
	}

	/**
	 * 打开相机
	 */
	fun openCamera() {
		// 获取照相机管理者
		mCameraManager = Utils.getApp().getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
		try {
			if (ActivityCompat.checkSelfPermission(Utils.getApp(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				return
			}
			// 打开相机
			mCameraManager?.openCamera(mCameraId!!, mStateCallback, mCameraHandler)
		} catch (e : CameraAccessException) {
			e.printStackTrace()
		}
	}

	/**
	 * 设置相机参数
	 *
	 * @param width  宽度
	 * @param height 高度
	 */
	fun setUpCamera(cameraId : Int, width : Int, height : Int) {
		// 为摄像头赋值
		mCameraId = cameraId.toString()
		// 创建Handler线程并启动
		val handlerThread = HandlerThread("Camera")
		handlerThread.start()
		// 创建Handler
		mCameraHandler = Handler(handlerThread.looper)
		// 获取摄像头的管理者
		mCameraManager = Utils.getApp().getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
		try {
			// 相机特性
			val cameraCharacteristics = mCameraManager!!.getCameraCharacteristics(mCameraId!!)
			// 获取StreamConfigurationMap，管理摄像头支持的所有输出格式和尺寸
			val map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
				?: return
			// 根据TextureView的尺寸设置预览尺寸
			mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
//			mPreviewSize = Size(width, height)
			// 获取相机支持的最大拍照尺寸
//			mCaptureSize = Collections.max(
//					Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)), object : Comparator<Size> {
//				override fun compare(lhs : Size, rhs : Size) : Int {
//					return java.lang.Long.signum((lhs.width*lhs.height-rhs.height*rhs.width).toLong())
//				}
//			})
			// 此处ImageReader用于拍照所需
			NLogger.d(TAG, " mPreviewSize = ${mPreviewSize.toString()} width =$width height =$height")
			setupImageReader(width, height)
		} catch (e : CameraAccessException) {
			e.printStackTrace()
			NLogger.d("mode", "失败 ${e.toString()}")
		}
	}

	private var post_index = 0
	private var limit_count = 2

	/**
	 * 设置ImageReader
	 */
	private fun setupImageReader(previewWidth : Int, previewHeight : Int) {
		// 2代表ImageReader中最多可以获取两帧图像流
		mImageReader = ImageReader.newInstance(previewWidth, previewHeight, ImageFormat.YUV_420_888, 1)
		// 设置图像可用监听
		mImageReader?.setOnImageAvailableListener({ reader : ImageReader ->
													  // 获取图片
													  val image = reader.acquireNextImage()
													  mOutCb?.onPreview(image)
													  if (post_index>=limit_count) {
														  post_index = 0
														  val nv21 = getBytesFromImageAsType(image, ImageFormat.NV21)
														  if (nv21 == null) {
															  NLogger.d(TAG, "nv21==null")
														  } else {
															  mICameraPreviewCb?.onPreview(nv21, previewWidth, previewHeight)
														  }
													  } else {
														  post_index++
													  }
													  image.close()
												  }, mCameraHandler)
	}

	/**
	 * 选择SizeMap中大于并且最接近width和height的size
	 *
	 * @param sizeMap 可选的尺寸
	 * @param width   宽
	 * @param height  高
	 * @return 最接近width和height的size
	 */
	private fun getOptimalSize(sizeMap : Array<Size>, width : Int, height : Int) : Size? {
		// 创建列表
		val sizeList : MutableList<Size> = ArrayList()
		// 遍历
		for (option in sizeMap) {
			// 判断宽度是否大于高度
			if (width>height) {
				if (option.width>width && option.height>height) {
					sizeList.add(option)
				}
			} else {
				if (option.width>height && option.height>width) {
					sizeList.add(option)
				}
			}
		}
		// 判断存储Size的列表是否有数据
		return if (sizeList.size>0) {
			Collections.min(sizeList, object : Comparator<Size> {
				override fun compare(lhs : Size, rhs : Size) : Int {
					return java.lang.Long.signum((lhs.width*lhs.height-rhs.width*rhs.height).toLong())
				}
			})
		} else sizeMap[0]
	}

	/**
	 * 预览
	 */
	private fun takePreview() {
		if (mPreviewSize == null || mImageReader == null) return
		// 设置默认的缓冲大小
		mSurfaceTexture?.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
		// 创建Surface
		val previewSurface = Surface(mSurfaceTexture)
		try {
			// 创建预览请求
			mCameraCaptureBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
			// 将previewSurface添加到预览请求中
			mCameraCaptureBuilder?.addTarget(previewSurface)
			mCameraCaptureBuilder?.addTarget(mImageReader!!.surface)
			// 创建会话
			mCameraDevice!!.createCaptureSession(Arrays.asList(previewSurface, mImageReader!!.surface), object : CameraCaptureSession.StateCallback() {
				override fun onConfigured(session : CameraCaptureSession) {
					try {
						// 設置session
						mCameraCaptureSession = session
						// 自动对焦
						mCameraCaptureBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
						// 配置
						val captureRequest = mCameraCaptureBuilder?.build()
							?: return
						// 设置重复预览请求
						mCameraCaptureSession?.setRepeatingRequest(captureRequest, null, mCameraHandler)
					} catch (e : CameraAccessException) {
						e.printStackTrace()
					}
				}

				override fun onConfigureFailed(session : CameraCaptureSession) {
					// 配置失败
				}
			}, mCameraHandler)
		} catch (e : CameraAccessException) {
			e.printStackTrace()
		}
	}

	fun closeCamera() {
		try {
			closePreviewSession()
			mCameraDevice?.close()
			mCameraDevice = null
		} catch (e : Exception) {
			e.printStackTrace()
		}
	}

	/**
	 * 关闭会话
	 */
	private fun closePreviewSession() {
		mCameraCaptureSession?.close()
		mCameraCaptureSession = null
	}

	fun getBytesFromImageAsType(image : Image, type : Int) : ByteArray? {
		try {
			//获取源数据，如果是YUV格式的数据planes.length = 3
			//plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
			val planes = image.planes
//数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
			// 所以我们只取width部分
			val width = image.width
			val height = image.height
			//此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
			val yuvBytes = ByteArray(width*height*ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)/8)
			//目标数组的装填到的位置
			var dstIndex = 0
			//临时存储uv数据的
			val uBytes = ByteArray(width*height/4)
			val vBytes = ByteArray(width*height/4)
			var uIndex = 0
			var vIndex = 0
			var pixelsStride : Int
			var rowStride : Int
			for (i in planes.indices) {
				pixelsStride = planes[i].pixelStride
				rowStride = planes[i].rowStride
				val buffer = planes[i].buffer
//如果pixelsStride==2，一般的Y的buffer长度=640*480，UV的长度=640*480/2-1
				//源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
				val bytes = ByteArray(buffer.capacity())
				buffer[bytes]
				var srcIndex = 0
				if (i == 0) {
					//直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
					for (j in 0 until height) {
						System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width)
						srcIndex += rowStride
						dstIndex += width
					}
				} else if (i == 1) {
					//根据pixelsStride取相应的数据
					for (j in 0 until height/2) {
						for (k in 0 until width/2) {
							uBytes[uIndex++] = bytes[srcIndex]
							srcIndex += pixelsStride
						}
						if (pixelsStride == 2) {
							srcIndex += rowStride-width
						} else if (pixelsStride == 1) {
							srcIndex += rowStride-width/2
						}
					}
				} else if (i == 2) {
					//根据pixelsStride取相应的数据
					for (j in 0 until height/2) {
						for (k in 0 until width/2) {
							vBytes[vIndex++] = bytes[srcIndex]
							srcIndex += pixelsStride
						}
						if (pixelsStride == 2) {
							srcIndex += rowStride-width
						} else if (pixelsStride == 1) {
							srcIndex += rowStride-width/2
						}
					}
				}
			}
			image.close()
			when (type) {
				ImageFormat.YUV_420_888 -> {
					System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.size)
					System.arraycopy(vBytes, 0, yuvBytes, dstIndex+uBytes.size, vBytes.size)
				}

				ImageFormat.YUV_422_888 -> {
					var i = 0
					while (i<vBytes.size) {
						yuvBytes[dstIndex++] = uBytes[i]
						yuvBytes[dstIndex++] = vBytes[i]
						i++
					}
				}

				ImageFormat.NV21        -> {
					var i = 0
					while (i<vBytes.size) {
						yuvBytes[dstIndex++] = vBytes[i]
						yuvBytes[dstIndex++] = uBytes[i]
						i++
					}
				}
			}
			return yuvBytes
		} catch (e : Exception) {
			image.close()
			NLogger.d(TAG, e.toString())
		}
		return null
	}

	fun setCameraPreviewCallBack(callback : ICamera2PreviewCallback?) {
		mOutCb = callback
	}
}