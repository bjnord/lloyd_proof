class RenameCorrectionCurrent < ActiveRecord::Migration
  def change
    rename_column :corrections, :current, :current_text
  end
end
