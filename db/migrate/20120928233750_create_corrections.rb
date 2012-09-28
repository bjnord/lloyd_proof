class CreateCorrections < ActiveRecord::Migration
  def change
    create_table :corrections do |t|
      t.text :current

      t.timestamps
    end
  end
end
