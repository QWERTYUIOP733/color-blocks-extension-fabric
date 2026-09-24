$colors = Get-Content "D:\文档\GitHub\游戏mod开发\colors\mard_295.json" -Raw | ConvertFrom-Json
$assetsDir = "D:\文档\GitHub\游戏mod开发\fabric\src\main\resources\assets\mard_pixel"

$blockstateTemplate = '{"variants":{"":{"model":"mard_pixel:block/color_block"}}}'
$itemModelTemplate = '{"parent":"mard_pixel:block/color_block"}'

foreach ($color in $colors.colors) {
    $code = $color.code.ToLower()
    $blockId = "color_block_$code"

    $blockstateTemplate | Out-File -FilePath "$assetsDir\blockstates\$blockId.json" -Encoding UTF8
    $itemModelTemplate | Out-File -FilePath "$assetsDir\models\item\$blockId.json" -Encoding UTF8
}

Write-Host "Created blockstates and item models for $($colors.colors.Count) color blocks"
