SECTION default {
	@intent1.setComponent("com.example.com.SampleProjectActivity")
	startActivity(@intent1)
}
SECTION com.example.com.SampleProjectActivity {
	$buttonPrint3.onClick()
	$buttonPrint3.onClick()

}

SECTION com.example.vdm.SampleProjectActivity {
//	button1.onClick()
//	button2.onClick()
	backButton
	changeLayout("Layout")

}

SECTION com.example.com.MylistView {

}

